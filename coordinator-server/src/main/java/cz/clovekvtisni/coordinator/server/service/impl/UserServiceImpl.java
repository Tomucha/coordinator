package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
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
        Objectify ofy = ofy();
        Key<UserEntity> userKey = systemService.findUniqueValueOwner(ofy, UniqueIndexEntity.Property.EMAIL, ValueTool.normalizeEmail(email));
        if (userKey == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        UserEntity userEntity = ofy.load().key(userKey).get();
        if (userEntity == null || password == null || !passwordHash(userEntity.getId(), password).equals(userEntity.getPassword())) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        appContext.setLoggedUser(userEntity);

        return userEntity;
    }

    @Override
    public UserEntity findById(Long id) {
        UserEntity userEntity = ofy().load().key(Key.create(UserEntity.class, id)).get();

        return userEntity;
    }

    @Override
    public ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark) {
        filter.setOrder("id");
        return ofy().findByFilter(filter, bookmark, limit);
    }

    private String passwordHash(Long userId, String password) {
        return SignatureTool.md5Digest(userId + "~" + PASSWORD_SEED + "~" + password);
    }

    @Override
    public UserEntity createUser(final UserEntity entity) {
        final MaObjectify ofy = ofy();
        return ofy.transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                logger.debug("creating " + entity);
                entity.setId(null);
                entity.setEmail(ValueTool.normalizeEmail(entity.getEmail()));
                ofy.save().entity(entity).now();

                entity.setPassword(passwordHash(entity.getId(), entity.getPassword()));
                ofy.save().entity(entity).now();

                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, entity.getEmail(), entity.getKey());
                return entity;
            }
        });
    }

    @Override
    public UserEntity updateUser(final UserEntity user) {
        final MaObjectify ofy = ofy();
        logger.debug("updating " + user);
        return ofy.transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                UserEntity toUpdate = ofy.load().key(Key.create(UserEntity.class, user.getId())).get();

                systemService.deleteUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, toUpdate.getEmail());
                systemService.saveUniqueIndexOwner(ofy, UniqueIndexEntity.Property.EMAIL, toUpdate.getEmail(), toUpdate.getKey());

                toUpdate.setFirstName(user.getFirstName());
                toUpdate.setLastName(user.getLastName());
                toUpdate.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                ofy.save().entity(toUpdate).now();
                return toUpdate;
            }
        });
    }

    @Override
    public void deleteUser(Long id) {
        ofy().delete().key(Key.create(UserEntity.class, id)).now();
    }

    @Override
    public void logout() {
        appContext.setLoggedUser(null);
    }
}
