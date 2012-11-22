package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 22.11.12
 */
public class UserInEventFilter extends NoDeletedFilter<UserInEventEntity> {
    @Override
    public Class<UserInEventEntity> getEntityClass() {
        return UserInEventEntity.class;
    }
}
