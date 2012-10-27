package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.security.Anonymous;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:14 PM
 */
public interface UserService extends Service {

    @Anonymous
    UserEntity login(String login, String password) throws MaPermissionDeniedException;

    @FilterResult("#helper.canRead(#entity)")
    UserEntity findById(Long id);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark);

    @CheckPermission("#helper.canCreate(#entity)")
    UserEntity createUser(UserEntity user);

    @CheckPermission("#helper.canUpdate(#entity)")
    UserEntity updateUser(UserEntity user);

    @CheckPermission("#helper.canDelete(new cz.clovekvtisni.coordinator.server.domain.UserEntity(#p0))")
    void deleteUser(Long id);

    @CheckPermission("@appContext.loggedUser != null")
    void logout();
}
