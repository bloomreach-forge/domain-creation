package org.onehippo.forge.domaincreation.authorization.model;

import java.util.List;


public interface Auth {

    List<Group> getAuthorGroups();

    List<Group> getEditorGroups();

    List<Group> getAdminGroups();

    String getChannel();

    List<Group> getGroups();

    boolean isAuthor();

    boolean isEditor();

    boolean isAdmin();

    List<DomainRule> getUnauthorizedDomains();

    List<DomainRule> getAuthorizedDomains();

}
