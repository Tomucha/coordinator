package cz.clovekvtisni.coordinator.server.security.permission;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.util.ClassTool;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 5/25/11
 * Time: 8:35 PM
 */
public abstract class Permission implements Serializable {

    private static final long serialVersionUID = -9163656055360743958L;

    private CoordinatorEntity entity;
    private String entityKindName;

    protected Permission() {
    }

    public Permission(CoordinatorEntity entity) {
        this.entity = entity;
        this.entityKindName = null;
    }

    public Permission(String entityKindName) {
        this.entityKindName = entityKindName;
        this.entity = null;
    }

    public CoordinatorEntity getEntity() {
        return entity;
    }

    public String getEntityKindName() {
        return entityKindName;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Permission that = (Permission) o;

        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        if (entityKindName != null ? !entityKindName.equals(that.entityKindName) : that.entityKindName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (entityKindName != null ? entityKindName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String s = ClassTool.simpleName(getClass());
        s += "{ " + (entity == null ? "NULL" : entity) + ", " + (entityKindName == null ? "" : entityKindName) + "}";
        return s;
    }

    public String getKind() {
        String id = ClassTool.simpleName(getClass());
        final String suffix = "Permission";
        if (!id.endsWith(suffix)) throw new IllegalStateException("class name doesn't end with " + suffix +
                ". Method getKind must be overwritten");
        return id.substring(0, id.length() - suffix.length());
    }
}
