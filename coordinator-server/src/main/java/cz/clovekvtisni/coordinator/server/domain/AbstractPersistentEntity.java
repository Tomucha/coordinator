package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.annotation.Index;
import cz.clovekvtisni.coordinator.domain.AbstractModifiableEntity;
import cz.clovekvtisni.coordinator.util.CloneTool;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public abstract class AbstractPersistentEntity<TARGET extends AbstractModifiableEntity, SELF extends CoordinatorEntity<SELF>> implements CoordinatorEntity<SELF> {

    @Index
    protected Date createdDate;

    @Index
    protected Date modifiedDate;

    protected Date deletedDate;

    protected abstract TARGET createTargetEntity();

    public AbstractPersistentEntity() {
    }

    public AbstractPersistentEntity(SELF entity) {
        CloneTool.cloneProperties(entity, this);
    }

    public TARGET buildTargetEntity() {
        TARGET entity = createTargetEntity();
        CloneTool.cloneProperties(this, entity);

        return entity;
    }

    public SELF populateFrom(TARGET entity) {
        CloneTool.cloneProperties(entity, this);
        return (SELF) this;
    }

    public SELF populateFrom(SELF entity) {
        CloneTool.cloneProperties(entity, this);
        return (SELF) this;
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
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public boolean isNew() {
        return getId() == null;
    }
}
