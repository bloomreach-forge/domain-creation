package org.onehippo.forge.domaincreation.authorization.model;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationAdapter implements Auth {

    private static Logger log = LoggerFactory.getLogger(AuthorizationAdapter.class);

    private final Node node;
    private boolean author;
    private boolean editor;
    private boolean admin;
    private Authorization authorization;

    public AuthorizationAdapter(final Node node) throws RepositoryException {
        this.node = node;
        if (node == null) {
            throw new NullPointerException("Node should not be null in authorization document (adapter)");
        }
        if (node.hasProperty("authorization:author")) {
            this.author = node.getProperty("authorization:author").getBoolean();
        }
        if (node.hasProperty("authorization:editor")) {
            this.editor = node.getProperty("authorization:editor").getBoolean();
        }
        if (node.hasProperty("authorization:admin")) {
            this.admin = node.getProperty("authorization:admin").getBoolean();
        }
        final String channel = node.getName();
        this.authorization = new Authorization();
        this.authorization.setAdmin(admin);
        this.authorization.setEditor(editor);
        this.authorization.setAuthor(author);
        this.authorization.setChannel(channel);
        getChildBeansByName("authorization:docfolders");

    }
    
    public void getChildBeansByName(String childNodeName) {
        NodeIterator nodes;
        try {
            nodes = node.getNodes(childNodeName);
            while (nodes.hasNext()) {
                Node child = nodes.nextNode();
                if (child == null) {
                    continue;
                }
                if (child.hasProperty("hippo:docbase")) {
                    final String path = child.getProperty("hippo:docbase").getNode().getPath();
                    if (!path.equals("/")) {
                        this.authorization.addAuthorized(new DomainRule(path));
                    }
                    Node parent = child.getProperty("hippo:docbase").getNode().getParent();
                    while (StringUtils.countMatches(parent.getPath(), "/") >= 3) {
                        this.authorization.addUnauthorized(new DomainRule(parent.getPath()));
                        parent = parent.getParent();

                    }
                }

            }
        } catch (RepositoryException e) {
            log.error("RepositoryException: Error while trying to create childBeans:", e);
        }
    }

    public List<Group> getAuthorGroups() {
        return this.authorization.getAuthorGroups();
    }

    public List<Group> getEditorGroups() {
        return this.authorization.getEditorGroups();

    }

    public List<Group> getAdminGroups() {
        return this.authorization.getAdminGroups();
    }

    public String getChannel() {
        return this.authorization.getChannel();
    }

    public List<Group> getGroups() {
        return this.authorization.getGroups();
    }

    public boolean isAuthor() {
        return this.authorization.isAuthor();
    }

    public boolean isEditor() {
        return this.authorization.isEditor();
    }

    public boolean isAdmin() {
        return this.authorization.isAdmin();
    }

    @Override
    public List<DomainRule> getUnauthorizedDomains() {
        return this.authorization.getUnauthorizedDomains();
    }

    @Override
    public List<DomainRule> getAuthorizedDomains() {
        return this.authorization.getAuthorizedDomains();
    }
}
