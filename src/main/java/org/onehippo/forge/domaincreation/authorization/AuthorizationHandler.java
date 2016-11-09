package org.onehippo.forge.domaincreation.authorization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TypeSafeTemplate;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.onehippo.forge.domaincreation.authorization.model.Auth;
import org.onehippo.forge.domaincreation.authorization.model.AuthorizationAdapter;

import org.apache.commons.io.IOUtils;
import org.hippoecm.repository.api.HippoSession;
import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.cms7.event.HippoEventConstants;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id$"
 */
public class AuthorizationHandler extends AbstractReconfigurableDaemonModule {

    private static Logger log = LoggerFactory.getLogger(AuthorizationHandler.class);

    private Session session;
    private boolean enabled = true;
    private Template template;

    @Override
    protected void doConfigure(final Node node) throws RepositoryException {
        if (node.hasProperty("auth.enabled")) {
            this.enabled = node.getProperty("auth.enabled").getBoolean();
        }
    }

    @Override
    protected void doInitialize(final Session session) throws RepositoryException {
        this.session = session;
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
    public void doAuthorizationUpdate(HippoEvent event) throws RepositoryException {
        if (HippoEventConstants.CATEGORY_WORKFLOW.equals(event.category()) &&
                ("publish".equals(event.get("methodName").toString())) && enabled) {
            AuthorizationWorkflowEvent workflowEvent = new AuthorizationWorkflowEvent(session, event);
            if ("authorization:authorization".equals(workflowEvent.documentType())) {
                InputStream stream = null;
                try {
                    Auth authorization = new AuthorizationAdapter(workflowEvent.getNode().getNode(workflowEvent.getNode().getName()));
                    final String importToConfigurationXML = template.as(AuthorizationTemplate.class).apply(authorization);
                    stream = new ByteArrayInputStream(importToConfigurationXML.getBytes(StandardCharsets.UTF_8));
                    final HippoSession hippoSession = (HippoSession) session;
                    hippoSession.importDereferencedXML("/", stream, 0, 5, 2);
                    hippoSession.save();
                } catch (RepositoryException e) {
                    log.error("repository exception while trying to apply authorization to master template", e);
                    session.refresh(false);
                } catch (IOException e) {
                    log.error("IO exception while trying to apply authorization to master template", e);
                    session.refresh(false);
                } finally {
                    IOUtils.closeQuietly(stream);
                }
            }
        }
    }

    public static interface AuthorizationTemplate extends TypeSafeTemplate<Auth> {
    }

    @Override
    protected void doShutdown() {
        if (this.session != null) {
            session.logout();
        }
        HippoServiceRegistry.unregisterService(this, HippoEventBus.class);
    }
}
