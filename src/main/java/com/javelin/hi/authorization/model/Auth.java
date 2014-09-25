package com.javelin.hi.authorization.model;

import java.util.List;

import javax.jcr.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
