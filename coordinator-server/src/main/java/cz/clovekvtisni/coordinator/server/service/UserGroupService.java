package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.filter.UserGroupFilter;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

import java.util.List;

public interface UserGroupService extends Service {

    @FilterResult("#helper.canRead(#entity)")
    UserGroupEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    List<UserGroupEntity> findByEventId(Long eventId, long flags);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<UserGroupEntity> findByFilter(UserGroupFilter filter, int limit, String bookmark, long flags);

    @FilterResult("#helper.canRead(#entity)")
    List<UserGroupEntity> findByIds(long flags, Long... ids);

    @CheckPermission("#helper.canCreate(#p0)")
    UserGroupEntity createUserGroup(UserGroupEntity inEvent);

    @CheckPermission("#helper.canUpdate(#p0)")
    UserGroupEntity updateUserGroup(UserGroupEntity inEvent);

    @CheckPermission("#helper.canDelete(new cz.clovekvtisni.coordinator.server.domain.UserGroup(#p0))")
    void deleteUserGroup(UserGroupEntity entity);

    void addUsersToGroup(UserGroupEntity entity, Long... userIds);
}
