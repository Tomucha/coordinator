package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import cz.clovekvtisni.coordinator.server.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:25 PM
 */
@Service("userService")
public class UserServiceImpl extends AbstractEntityServiceImpl implements UserService {

    @Override
    public UserEntity login(String login, String password) {
        //TODO
        UserEntity user = new UserEntity();
        user.setLogin(login);
        user.setFirstName("fake");
        return user;
    }

    @Override
    public UserEntity findById(Long id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteUser(Long id) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
