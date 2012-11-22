package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

public interface UserInEventService extends Service {

    public static final long FLAG_FETCH_EVENT = 1l;

    @FilterResult("#helper.canRead(#entity)")
    UserInEventEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<UserInEventEntity> findByFilter(UserInEventFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#entity)")
    UserInEventEntity create(UserInEventEntity inEvent);

    @CheckPermission("#helper.canUpdate(#entity)")
    UserInEventEntity update(UserInEventEntity inEvent);
}
