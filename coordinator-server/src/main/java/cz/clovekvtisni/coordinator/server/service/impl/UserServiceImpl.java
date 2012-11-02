package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.util.SignatureTool;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:25 PM
 */
@Service("userService")
public class UserServiceImpl extends AbstractEntityServiceImpl implements UserService {

    private static final String PASSWORD_SEED = "e{\"DFGP:2354\":asdlghH%$~23'5;'";

    @Override
    public User login(String email, String password) {
        Objectify ofy = noTransactionalObjectify();
        Key<UserEntity> userKey = systemService.findUniqueValueOwner(ofy, UniqueIndexEntity.Property.EMAIL, ValueTool.normalizeEmail(email));
        if (userKey == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        UserEntity userEntity = ofy.find(userKey);
        if (userEntity == null || password == null || !passwordHash(userEntity.getId(), password).equals(userEntity.getPassword())) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        User user = userEntity.buildUser();
        appContext.setLoggedUser(user);

        return user;
    }

    @Override
    public User findById(Long id) {
        UserEntity userEntity = noTransactionalObjectify().find(UserEntity.class, id);

        return userEntity.buildUser();
    }

    @Override
    public ResultList<User> findByFilter(UserFilter filter, int limit, String bookmark) {
        Query<UserEntity> query = noTransactionalObjectify().query(UserEntity.class);
        if (!ValueTool.isEmpty(filter.getEmail())) {
            query.filter("email =", filter.getEmail().toLowerCase());
        }

        //TODO: ordering (support in AbstractFilter)
        query.order("id");

        if (!ValueTool.isEmpty(bookmark)) {
            query.startCursor(Cursor.fromWebSafeString(bookmark));
        }

        QueryResultIterator<UserEntity> iterator = query.iterator();
        List<User> users = new ArrayList<User>();
        while (iterator.hasNext()) {
            UserEntity userEntity = iterator.next();
            users.add(userEntity.buildUser());
            if (--limit <= 0) {
                return new ResultList<User>(users, iterator.getCursor().toWebSafeString());
            }
        }

        return new ResultList<User>(users, null);
    }

    private String passwordHash(Long userId, String password) {
        return SignatureTool.md5Digest(userId + "~" + PASSWORD_SEED + "~" + password);
    }

    @Override
    public User createUser(User newUser) {
        final UserEntity user = new UserEntity(newUser);
        return transactionWithResult("creating " + user, new TransactionWithResultCallback<User>() {
            @Override
            public User runInTransaction(Objectify ofy) {
                user.setId(null);
                user.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                ofy.put(user);

                user.setPassword(passwordHash(user.getId(), user.getPassword()));
                ofy.put(user);

                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, user.getEmail(), user.getKey());
                return user.buildUser();
            }
        });
    }

    @Override
    public User updateUser(User updatedUser) {
        final UserEntity user = new UserEntity(updatedUser);
        return transactionWithResult("updating " + user, new TransactionWithResultCallback<User>() {
            @Override
            public User runInTransaction(Objectify ofy) {
                UserEntity toUpdate = ofy.find(UserEntity.class, user.getId());

                systemService.deleteUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, toUpdate.getEmail());
                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.LOGIN, toUpdate.getEmail(), toUpdate.getKey());

                toUpdate.setFirstName(user.getFirstName());
                toUpdate.setLastName(user.getLastName());
                toUpdate.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                ofy.put(toUpdate);
                return toUpdate.buildUser();
            }
        });
    }

    @Override
    public void deleteUser(Long id) {
        noTransactionalObjectify().delete(UserEntity.class, id);
    }

    @Override
    public void logout() {
        appContext.setLoggedUser(null);
    }
}
