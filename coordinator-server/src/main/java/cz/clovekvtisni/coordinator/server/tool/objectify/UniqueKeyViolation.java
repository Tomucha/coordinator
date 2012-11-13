package cz.clovekvtisni.coordinator.server.tool.objectify;

import com.googlecode.objectify.Key;
import cz.clovekvtisni.coordinator.exception.ErrorCode;
import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 13.11.12
 */
public class UniqueKeyViolation extends MaException {

    private UniqueIndexEntity.Property property;

    private Key<? extends CoordinatorEntity> ownerKey;

    public UniqueKeyViolation(UniqueIndexEntity.Property property, Key<? extends CoordinatorEntity> ownerKey) {
        this.property = property;
        this.ownerKey = ownerKey;
    }

    public UniqueIndexEntity.Property getProperty() {
        return property;
    }

    public void setProperty(UniqueIndexEntity.Property property) {
        this.property = property;
    }

    public Key<? extends CoordinatorEntity> getOwnerKey() {
        return ownerKey;
    }

    public void setOwnerKey(Key<? extends CoordinatorEntity> ownerKey) {
        this.ownerKey = ownerKey;
    }
}
