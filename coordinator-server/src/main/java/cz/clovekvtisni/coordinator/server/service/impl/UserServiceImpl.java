package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
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
    public UserEntity login(String login, String password) {
        Objectify ofy = noTransactionalObjectify();
        Key<UserEntity> userKey = systemService.findUniqueValueOwner(ofy, UniqueIndexEntity.Property.LOGIN, ValueTool.normalizeLogin(login));
        if (userKey == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        UserEntity userEntity = ofy.find(userKey);
        if (userEntity == null || password == null || !passwordHash(userEntity.getId(), password).equals(userEntity.getPassword())) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        appContext.setLoggedUser(userEntity);

        return userEntity;
    }

    @Override
    public UserEntity findById(Long id) {
        return noTransactionalObjectify().find(UserEntity.class, id);
    }

    @Override
    public ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark) {
        Query<UserEntity> query = noTransactionalObjectify().query(UserEntity.class);
        if (!ValueTool.isEmpty(filter.getLogin())) {
            query.filter("login =", filter.getLogin().toLowerCase());
        }

        //TODO: ordering (support in AbstractFilter)
        query.order("id");

        if (!ValueTool.isEmpty(bookmark)) {
            query.startCursor(Cursor.fromWebSafeString(bookmark));
        }

        QueryResultIterator<UserEntity> iterator = query.iterator();
        List<UserEntity> userEntities = new ArrayList<UserEntity>();
        while (iterator.hasNext()) {
            UserEntity userEntity = iterator.next();
            userEntities.add(userEntity);
            if (--limit <= 0) {
                return new ResultList<UserEntity>(userEntities, iterator.getCursor().toWebSafeString());
            }
        }

        return new ResultList<UserEntity>(userEntities, null);
    }

    private String passwordHash(Long userId, String password) {
        return SignatureTool.md5Digest(userId + "~" + PASSWORD_SEED + "~" + password);
    }

    @Override
    public UserEntity createUser(final UserEntity user) {
        return transactionWithResult("creating " + user, new TransactionWithResultCallback<UserEntity>() {
            @Override
            public UserEntity runInTransaction(Objectify ofy) {
                user.setId(null);
                user.setLogin(ValueTool.normalizeLogin(user.getLogin()));
                user.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                ofy.put(user);

                user.setPassword(passwordHash(user.getId(), user.getPassword()));
                ofy.put(user);

                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.LOGIN, user.getLogin(), user.getKey());
                return user;
            }
        });
    }

    @Override
    public UserEntity updateUser(final UserEntity user) {
        return transactionWithResult("updating " + user, new TransactionWithResultCallback<UserEntity>() {
            @Override
            public UserEntity runInTransaction(Objectify ofy) {
                UserEntity toUpdate = ofy.find(UserEntity.class, user.getId());

                systemService.deleteUniqueIndexOwner(ofy, UniqueIndexEntity.Property.LOGIN, toUpdate.getLogin());
                toUpdate.setLogin(ValueTool.normalizeLogin(user.getLogin()));
                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.LOGIN, toUpdate.getLogin(), toUpdate.getKey());

                toUpdate.setFirstName(user.getFirstName());
                toUpdate.setLastName(user.getLastName());
                toUpdate.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                ofy.put(toUpdate);
                return toUpdate;
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
