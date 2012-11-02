package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
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
    User login(String login, String password) throws MaPermissionDeniedException;

    @FilterResult("#helper.canRead(#entity)")
    User findById(Long id);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<User> findByFilter(UserFilter filter, int limit, String bookmark);

    @CheckPermission("#helper.canCreate(#entity)")
    User createUser(User user);

    @CheckPermission("#helper.canUpdate(#entity)")
    User updateUser(User user);

    @CheckPermission("#helper.canDelete(new cz.clovekvtisni.coordinator.server.domain.User(#p0))")
    void deleteUser(Long id);

    @CheckPermission("@appContext.loggedUser != null")
    void logout();
}
