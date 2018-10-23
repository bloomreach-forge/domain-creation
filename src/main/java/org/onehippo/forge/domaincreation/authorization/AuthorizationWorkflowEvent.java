package org.onehippo.forge.domaincreation.authorization;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.repository.events.HippoWorkflowEvent;


public class AuthorizationWorkflowEvent<T extends AuthorizationWorkflowEvent<T>> extends HippoWorkflowEvent<T> {

    private Session session;

    public AuthorizationWorkflowEvent(final Session session, HippoEvent event) {
        super(event);
        this.session = session;
    }

    public Node getNode() throws RepositoryException {
        if (session != null && session.nodeExists(subjectPath())) {
            return session.getNode(subjectPath());
        }
        throw new ItemNotFoundException("workflow node not found, please unpublish and republish");
    }

}
