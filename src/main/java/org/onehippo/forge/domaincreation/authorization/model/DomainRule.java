package org.onehippo.forge.domaincreation.authorization.model;

/**
 * @version "$Id$"
 */
public class DomainRule {

    public String pathId;
    public String path;

    public DomainRule(final String path) {
        this.path = path;
        this.pathId = this.path.replace("/", "-").substring(1);
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(final String pathId) {
        this.pathId = pathId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Define equals method so duplicates can be eliminated
     * @param obj
     * @return
     */

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof DomainRule)) {
            return false;
        }
        DomainRule domainObj = (DomainRule) obj;

        return (domainObj.getPathId().equals(pathId) &&
                domainObj.getPath().equals(path));
    }
}
