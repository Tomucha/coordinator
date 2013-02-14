package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UserAuthKey;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.security.Anonymous;
import cz.clovekvtisni.coordinator.server.security.CheckPermission;
import cz.clovekvtisni.coordinator.server.security.FilterResult;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:14 PM
 */
public interface UserService extends Service {

    public static final long FLAG_FETCH_EQUIPMENT = 1l;

    public static final long FLAG_FETCH_SKILLS = 2l;

    public static final long FLAG_FORCE_REGISTRATION = 4l;

    @Anonymous
    UserEntity login(String login, String password, String... hasRoles) throws MaPermissionDeniedException;

    @FilterResult("#helper.canRead(#entity)")
    UserEntity findById(Long id, long flags);

    @FilterResult("#helper.canRead(#entity)")
    List<UserEntity> findByIds(long flags, Long... ids);

    @FilterResult("#helper.canRead(#entity)")
    ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark, long flags);

    @CheckPermission("#helper.canCreate(#p0)")
    UserEntity createUser(UserEntity entity);

    @CheckPermission("#helper.canUpdate(#p0)")
    UserEntity updateUser(UserEntity user);

    @CheckPermission("#helper.canDelete(new cz.clovekvtisni.coordinator.server.domain.UserEntity(#p0))")
    void deleteUser(Long id);

    @CheckPermission("#helper.canDelete(new cz.clovekvtisni.coordinator.server.domain.UserEntity(#p0))")
    UserEntity suspendUser(Long id, String reason, long flags);

    @CheckPermission("@appContext.loggedUser != null")
    void logout();

    @Anonymous
    UserAuthKey createAuthKey(UserEntity user);

    @Anonymous
    UserEntity getByAuthKey(String key);

    @Anonymous(Anonymous.Mode.PROPAGATE)
    UserEntity preRegister(UserEntity newUser, long flags, boolean systemCall);

    @Anonymous(Anonymous.Mode.PROPAGATE)
    UserInEventEntity register(UserEntity newUser, UserInEventEntity event, long flags);
}
