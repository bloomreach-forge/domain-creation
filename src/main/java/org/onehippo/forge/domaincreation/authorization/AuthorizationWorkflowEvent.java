package org.onehippo.forge.domaincreation.authorization;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id$"
 */
public class AuthorizationWorkflowEvent<T extends AuthorizationWorkflowEvent<T>> extends HippoWorkflowEvent<T> {

    private static Logger log = LoggerFactory.getLogger(AuthorizationWorkflowEvent.class);

    private Session session;

    public AuthorizationWorkflowEvent(final Session session, HippoEvent event) {
        super(event);
        this.session = session;
    }

    public String getPrimaryNodeType() throws RepositoryException {
        return getNode().getPrimaryNodeType().getName();
    }

    public Node getNode() throws RepositoryException {
        if (session != null && session.nodeExists(documentPath())) {
            return session.getNode(documentPath());
        }
        throw new ItemNotFoundException("workflow node not found, please unpublish and republish");
    }

}
