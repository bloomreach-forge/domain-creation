package org.onehippo.forge.domaincreation.authorization.model;

import java.util.List;

/**
 * @version "$Id$"
 */
public interface Auth {

    public List<Group> getAuthorGroups();

    public List<Group> getEditorGroups();

    public List<Group> getAdminGroups();

    public String getChannel();

    public List<Group> getGroups();

    public boolean isAuthor();

    public boolean isEditor();

    public boolean isAdmin();

    public List<DomainRule> getUnauthorizedDomains();

    public List<DomainRule> getAuthorizedDomains();

}
