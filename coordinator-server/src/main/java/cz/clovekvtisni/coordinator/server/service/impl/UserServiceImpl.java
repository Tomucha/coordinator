package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.Cursor;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.filter.result.NoDeletedFilter;
import cz.clovekvtisni.coordinator.util.SignatureTool;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.stereotype.Service;

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
    public UserEntity login(String email, String password) {
        Objectify ofy = noTransactionalObjectify();
        Key<UserEntity> userKey = systemService.findUniqueValueOwner(ofy, UniqueIndexEntity.Property.EMAIL, ValueTool.normalizeEmail(email));
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
        UserEntity userEntity = noTransactionalObjectify().find(UserEntity.class, id);

        return userEntity;
    }

    @Override
    public ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark) {
        filter.setOrder("id");
        return noTransactionalObjectify().getResult(UserEntity.class, filter, bookmark, limit, new NoDeletedFilter());
    }

    private String passwordHash(Long userId, String password) {
        return SignatureTool.md5Digest(userId + "~" + PASSWORD_SEED + "~" + password);
    }

    @Override
    public UserEntity createUser(final UserEntity entity) {
        return transactionWithResult("creating " + entity, new TransactionWithResultCallback<UserEntity>() {
            @Override
            public UserEntity runInTransaction(Objectify ofy) {
                entity.setId(null);
                entity.setEmail(ValueTool.normalizeEmail(entity.getEmail()));
                ofy.put(entity);

                entity.setPassword(passwordHash(entity.getId(), entity.getPassword()));
                ofy.put(entity);

                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, entity.getEmail(), entity.getKey());
                return entity;
            }
        });
    }

    @Override
    public UserEntity updateUser(final UserEntity user) {
        return transactionWithResult("updating " + user, new TransactionWithResultCallback<UserEntity>() {
            @Override
            public UserEntity runInTransaction(Objectify ofy) {
                UserEntity toUpdate = ofy.find(UserEntity.class, user.getId());

                systemService.deleteUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, toUpdate.getEmail());
                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, toUpdate.getEmail(), toUpdate.getKey());

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
