package org.onehippo.forge.domaincreation.authorization.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version "$Id$"
 */
public class Group {

    private static Logger log = LoggerFactory.getLogger(Group.class);

    public Group(final String group) {
        this.group = group;
    }

    public String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

}
