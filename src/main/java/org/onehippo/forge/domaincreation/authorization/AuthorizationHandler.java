package org.onehippo.forge.domaincreation.authorization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.value.StringValue;
import org.hippoecm.repository.api.HippoSession;
import org.hippoecm.repository.api.ImportReferenceBehavior;
import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.cms7.event.HippoEventConstants;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.forge.domaincreation.authorization.model.Auth;
import org.onehippo.forge.domaincreation.authorization.model.AuthorizationAdapter;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TypeSafeTemplate;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

/**
 * @version "$Id$"
 */
public class AuthorizationHandler extends AbstractReconfigurableDaemonModule {

    private static Logger log = LoggerFactory.getLogger(AuthorizationHandler.class);
    private static String GROUPS_PATH = "hippo:configuration/hippo:groups";
    private static String DOMAINS_PATH = "hippo:configuration/hippo:domains";
    private static String MEMBERS_PROPERTY = "hipposys:members";
    private HippoSession hippoSession;

    private boolean enabled = true;
    private Template template;

    @Override
    protected void doConfigure(final Node node) throws RepositoryException {
        if (node.hasProperty("auth.enabled")) {
            this.enabled = node.getProperty("auth.enabled").getBoolean();
        }
    }

    @Override
    protected void doInitialize(final Session session) {
        this.hippoSession = (HippoSession) session;
        HippoServiceRegistry.registerService(this, HippoEventBus.class);
        TemplateLoader masterLoader = new ClassPathTemplateLoader();
        masterLoader.setPrefix("/templates");
        masterLoader.setSuffix(".xml");
        Handlebars masterHandleBars = new Handlebars(masterLoader);
        try {
            template = masterHandleBars.compile("master");
        } catch (IOException e) {
            log.error("Unable to find master.xml template", e);
        }
    }

    @Subscribe
    public void doAuthorizationUpdate(HippoEvent event) {
        if (HippoEventConstants.CATEGORY_WORKFLOW.equals(event.category()) &&
                ("publish".equals(event.get("methodName").toString())) && enabled) {
            AuthorizationWorkflowEvent workflowEvent = new AuthorizationWorkflowEvent(hippoSession, event);
            if ("authorization:authorization".equals(workflowEvent.documentType())) {
                InputStream stream = null;
                try {
                    Auth authorization = new AuthorizationAdapter(workflowEvent.getNode().getNode(workflowEvent.getNode().getName()));
                    final String importToConfigurationXML = template.as(AuthorizationTemplate.class).apply(authorization);
                    stream = new ByteArrayInputStream(importToConfigurationXML.getBytes(StandardCharsets.UTF_8));
                    //remember previously added group members
                    final HashMap<String, List<String>> groupMembers = getGroupMembers(hippoSession, authorization);
                    //clear nodes that might have been added previously to avoid SNS errors
                    clearPreviousAuthConfig(hippoSession, authorization);
                    //AFAIC last param can be null bc we don't refer to other files in XML imports
                    hippoSession.importEnhancedSystemViewXML("/", stream, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW, ImportReferenceBehavior.IMPORT_REFERENCE_NOT_FOUND_REMOVE, null);
                    //add the previous members to groups
                    addPreviousMembers(hippoSession, authorization, groupMembers);
                    hippoSession.save();
                } catch (RepositoryException | IOException e) {
                    log.error(e.getMessage());
                } finally {
                    IOUtils.closeQuietly(stream);
                }
            }
        }
    }

    private void addPreviousMembers(HippoSession hippoSession, Auth authorization, HashMap<String, List<String>> groupMembers) {
        try {
            if (authorization.isAdmin()) {
                String nodePath = GROUPS_PATH + "/" + authorization.getChannel() + "-admins";
                if (hippoSession.getRootNode().hasNode(nodePath)) {
                    ArrayList<Value> values = new ArrayList<>();
                    for (String member : groupMembers.get("admin")) {
                        values.add(new StringValue(member));
                    }
                    if (values.size() > 0) {
                        hippoSession.getRootNode().getNode(nodePath).setProperty(MEMBERS_PROPERTY, values.toArray(new Value[values.size()]));
                    }
                }
            }
            if (authorization.isAuthor()) {
                String nodePath = GROUPS_PATH + "/" + authorization.getChannel() + "-authors";
                if (hippoSession.getRootNode().hasNode(nodePath)) {
                    ArrayList<Value> values = new ArrayList<>();
                    for (String member : groupMembers.get("author")) {
                        values.add(new StringValue(member));
                    }
                    if (values.size() > 0) {
                        hippoSession.getRootNode().getNode(nodePath).setProperty(MEMBERS_PROPERTY, values.toArray(new Value[values.size()]));
                    }
                }
            }
            if (authorization.isEditor()) {
                String nodePath = GROUPS_PATH + "/" + authorization.getChannel() + "-editors";
                if (hippoSession.getRootNode().hasNode(nodePath)) {
                    ArrayList<Value> values = new ArrayList<>();
                    for (String member : groupMembers.get("editor")) {
                        values.add(new StringValue(member));
                    }
                    if (values.size() > 0) {
                        hippoSession.getRootNode().getNode(nodePath).setProperty(MEMBERS_PROPERTY, values.toArray(new Value[values.size()]));
                    }
                }
            }

        } catch (RepositoryException e) {
            log.error("Repository exception in addPreviousMembers method: {}", e.getMessage());
        }
    }

    private HashMap<String, List<String>> getGroupMembers(HippoSession hippoSession, Auth authorization) {
        //remember previously added members
        HashMap<String, List<String>> groupMemberMaps = new HashMap<>();
        groupMemberMaps.put("admin", new ArrayList<>());
        groupMemberMaps.put("author", new ArrayList<>());
        groupMemberMaps.put("editor", new ArrayList<>());
        try {
            if (authorization.isAdmin()) {
                String nodePath = GROUPS_PATH + "/" + authorization.getChannel() + "-admins";
                if (hippoSession.getRootNode().hasNode(nodePath)) {
                    final Node node = hippoSession.getRootNode().getNode(nodePath);
                    if (node.hasProperty(MEMBERS_PROPERTY)) {
                        final Value[] values = node.getProperty(MEMBERS_PROPERTY).getValues();
                        for (Value value : values) {
                            final String member = value.getString();
                            groupMemberMaps.get("admin").add(member);
                        }
                    }
                }
            }
            if (authorization.isAuthor()) {
                String nodePath = GROUPS_PATH + "/" + authorization.getChannel() + "-authors";
                if (hippoSession.getRootNode().hasNode(nodePath)) {
                    final Node node = hippoSession.getRootNode().getNode(nodePath);
                    if (node.hasProperty(MEMBERS_PROPERTY)) {
                        final Value[] values = node.getProperty(MEMBERS_PROPERTY).getValues();
                        for (Value value : values) {
                            final String member = value.getString();
                            groupMemberMaps.get("author").add(member);
                        }
                    }
                }
            }
            if (authorization.isEditor()) {
                String nodePath = GROUPS_PATH + "/" + authorization.getChannel() + "-editors";
                if (hippoSession.getRootNode().hasNode(nodePath)) {
                    final Node node = hippoSession.getRootNode().getNode(nodePath);
                    if (node.hasProperty(MEMBERS_PROPERTY)) {
                        final Value[] values = node.getProperty(MEMBERS_PROPERTY).getValues();
                        for (Value value : values) {
                            final String member = value.getString();
                            groupMemberMaps.get("editor").add(member);
                        }
                    }
                }
            }
        } catch (RepositoryException e) {
            log.error("Repository exception in getGroupMembers method: {}", e.getMessage());
        }
        return groupMemberMaps;
    }

    private void clearPreviousAuthConfig(HippoSession hippoSession, Auth authorization) {
        try {
            //clear domains and groups
            String[] paths = {DOMAINS_PATH, GROUPS_PATH};
            for (String path : paths) {
                final NodeIterator nodes = hippoSession.getRootNode().getNode(path).getNodes(authorization.getChannel() + "-*");
                while (nodes.hasNext()) {
                    final Node node = nodes.nextNode();
                    node.remove();
                }
            }
        } catch (RepositoryException e) {
            log.error("Repository exception in clearPreviousAuthConfig method: {}", e.getMessage());
        }
    }

    interface AuthorizationTemplate extends TypeSafeTemplate<Auth> {
    }

    @Override
    protected void doShutdown() {
        if (this.session != null && this.session.isLive()) {
            session.logout();
        }
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }
}