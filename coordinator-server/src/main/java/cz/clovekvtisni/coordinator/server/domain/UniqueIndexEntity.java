package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

import com.googlecode.objectify.annotation.Id;;

@Cache
@Entity(name = "UniqueIndex")
public class UniqueIndexEntity {

    public static enum Property {
   		// PORADI SE NESMI MENIT A NESMI SE ODTUD MAZAT! ordinal se pouziva v databazi
   		EMAIL, USER_EQUIPMENT
   	}

    @Id
   	private String uniqueKey;

   	@Index
   	private Key<? extends CoordinatorEntity> entityKey;


	public static Key<UniqueIndexEntity> createKey(Property property, String uniqueValue) {
        return Key.create(UniqueIndexEntity.class, property.ordinal()+"#"+uniqueValue);
	}

	public UniqueIndexEntity() {
	}
	
	public UniqueIndexEntity(Key<UniqueIndexEntity> uniqueValue, Key<? extends CoordinatorEntity> entityKey) {
		this.uniqueKey = uniqueValue.getName();
		this.entityKey = entityKey;
	}
	
	public Key<? extends CoordinatorEntity>  getEntityKey() {
		return entityKey;
	}

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setEntityKey(Key<? extends CoordinatorEntity> entityKey) {
		this.entityKey = entityKey;
	}

}