package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:14 PM
 */
public interface UserService extends Service {

    UserEntity login(String login, String password);

    UserEntity findById(Long id);

    ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark);

    UserEntity createUser(UserEntity user);

    UserEntity updateUser(UserEntity user);

    void deleteUser(Long id);
}
