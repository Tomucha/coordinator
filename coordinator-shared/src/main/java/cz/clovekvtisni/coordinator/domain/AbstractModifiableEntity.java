package cz.clovekvtisni.coordinator.domain;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 2.11.12
 */
public abstract class AbstractModifiableEntity implements Entity {

    protected Long id;

    private Date createdDate;

    private Date modifiedDate;

    private Date deletedDate;

    final public Long getId() {
        return id;
    }

    final public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public boolean isDeleted() {
        return deletedDate != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractModifiableEntity that = (AbstractModifiableEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
