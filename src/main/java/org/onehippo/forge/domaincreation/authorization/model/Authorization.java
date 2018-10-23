package org.onehippo.forge.domaincreation.authorization.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @version "$Id$"
 */
public class Authorization implements Auth {

    public String channel;
    public List<DomainRule> authorizedDomains;
    public List<DomainRule> unauthorizedDomains;
    public List<Group> groups;
    public List<Group> authorGroups;
    public List<Group> editorGroups;
    public List<Group> adminGroups;
    public boolean author;
    public boolean editor;
    public boolean admin;


    public List<Group> getAuthorGroups() {
        if (author) {
            authorGroups = new ArrayList<>();
            authorGroups.add(new Group(channel + "-authors"));
            if (editor) {
                authorGroups.add(new Group(channel + "-editors"));
            }
            if (admin) {
                authorGroups.add(new Group(channel + "-admins"));
            }
        }
        return authorGroups;
    }

    public List<Group> getEditorGroups() {
        if (editor) {
            editorGroups = new ArrayList<>();
            editorGroups.add(new Group(channel + "-editors"));
            if (admin) {
                editorGroups.add(new Group(channel + "-admins"));
            }
        }
        return editorGroups;
    }

    public List<Group> getAdminGroups() {
        if (admin) {
            adminGroups = new ArrayList<>();
            adminGroups.add(new Group(channel + "-admins"));
        }
        return adminGroups;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public List<DomainRule> getAuthorizedDomains() {
        return authorizedDomains;
    }

    public List<Group> getGroups() {
        groups = new ArrayList<>();
        if (author) {
            groups.add(new Group(channel + "-authors"));
        }
        if (editor) {
            groups.add(new Group(channel + "-editors"));
        }
        if (admin) {
            groups.add(new Group(channel + "-admins"));
        }

        return groups;
    }

    public void setGroups(final List<Group> groups) {
        this.groups = groups;
    }

    public boolean add(final Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        return groups.add(group);
    }

    public boolean addAuthorized(final DomainRule domain) {
        if (authorizedDomains == null) {
            authorizedDomains = new ArrayList<>();
        }
        return !authorizedDomains.contains(domain) && authorizedDomains.add(domain);
    }

    public boolean addUnauthorized(final DomainRule domain) {
        if (unauthorizedDomains == null) {
            unauthorizedDomains = new ArrayList<>();
        }
        return !unauthorizedDomains.contains(domain) && unauthorizedDomains.add(domain);
    }

    public boolean isAuthor() {
        return author;
    }

    public void setAuthor(final boolean author) {
        this.author = author;
    }

    public boolean isEditor() {
        return editor;
    }

    public void setEditor(final boolean editor) {
        this.editor = editor;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public List<DomainRule> getUnauthorizedDomains() {
        return unauthorizedDomains;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

}
