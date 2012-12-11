package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserInEventFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

public interface UserInEventService extends Service {

    public static final long FLAG_FETCH_EVENT = 1l;

    public static final long FLAG_FETCH_USER = 2l;

    @FilterResult("#helper.canRead(#entity)")
    UserInEventEntity findById(Long id, Long parentUserId, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<UserInEventEntity> findByFilter(UserInEventFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#p0)")
    UserInEventEntity create(UserInEventEntity inEvent);

    @CheckPermission("#helper.canUpdate(#p0)")
    UserInEventEntity update(UserInEventEntity inEvent);
}
