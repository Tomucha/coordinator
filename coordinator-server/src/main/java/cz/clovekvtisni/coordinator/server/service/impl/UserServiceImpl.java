package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.server.domain.UniqueIndexEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.filter.EquipmentFilter;
import cz.clovekvtisni.coordinator.server.filter.UserEquipmentFilter;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.util.SignatureTool;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
        Key<UserEntity> userKey = systemService.findUniqueValueOwner(ofy(), UniqueIndexEntity.Property.EMAIL, ValueTool.normalizeEmail(email));
        if (userKey == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        UserEntity userEntity = ofy().load().key(userKey).get();
        if (userEntity == null || password == null || !passwordHash(userEntity.getId(), password).equals(userEntity.getPassword())) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        appContext.setLoggedUser(userEntity);

        return userEntity;
    }

    @Override
    public UserEntity findById(Long id, long flags) {
        UserEntity userEntity = ofy().load().key(Key.create(UserEntity.class, id)).get();

        populateUser(userEntity, flags);

        return userEntity;
    }

    private void populateUser(UserEntity userEntity, long flags) {
        if ((flags & FLAG_FETCH_EQUIPMENT) != 0) {
            UserEquipmentFilter filter = new UserEquipmentFilter();
            filter.setUserIdVal(userEntity.getId());
            ResultList<UserEquipmentEntity> equipments = ofy().findByFilter(filter, null, 0);
            if (equipments.getResultSize() > 0) {
                userEntity.setEquipmentList(equipments.getResult().toArray(new UserEquipmentEntity[0]));
            } else {
                userEntity.setEquipmentList(new UserEquipmentEntity[0]);
            }
        }
    }

    @Override
    public ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark, long flags) {
        filter.setOrder("id");
        return ofy().findByFilter(filter, bookmark, limit);
    }

    private String passwordHash(Long userId, String password) {
        return SignatureTool.md5Digest(userId + "~" + PASSWORD_SEED + "~" + password);
    }

    @Override
    public UserEntity createUser(final UserEntity entity) {
        return ofy().transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                logger.debug("creating " + entity);
                entity.setId(null);
                entity.setEmail(ValueTool.normalizeEmail(entity.getEmail()));
                updateSystemFields(entity);
                ofy().save().entity(entity).now();

                entity.setPassword(passwordHash(entity.getId(), entity.getPassword()));
                ofy().save().entity(entity).now();

                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, entity.getEmail(), entity.getKey());

                saveFields(entity, true);

                return entity;
            }
        });
    }

    @Override
    public UserEntity updateUser(final UserEntity user) {
        logger.debug("updating " + user);
        return ofy().transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                //UserEntity toUpdate = ofy().load().key(Key.create(UserEntity.class, user.getId())).get();
                updateSystemFields(user);
                systemService.deleteUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, user.getEmail());
                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, user.getEmail(), user.getKey());

                user.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                ofy().put(user);

                saveFields(user, false);

                return user;
            }
        });
    }

    private void saveFields(UserEntity entity, boolean isNew) {
        if (entity.getEquipmentList() != null) {
            for (UserEquipmentEntity equipmentEntity : entity.getEquipmentList()) {
                if (equipmentEntity.isDeleted()) {
                    if (equipmentEntity.isNew())
                        continue;
                    ofy().delete(equipmentEntity);

                } else {
                    equipmentEntity.setParentKey(entity.getKey());
                    equipmentEntity.setUserId(entity.getId());
                    updateSystemFields(equipmentEntity);

                    /*
                    if (!isNew) {
                        systemService.deleteUniqueIndexOwner(
                                ofy(),
                                UniqueIndexEntity.Property.USER_EQUIPMENT,
                                equipmentEntity.getUserId() + "+" + equipmentEntity.getEquipmentId()
                        );
                    }
                    systemService.saveUniqueIndexOwner(
                            ofy(),
                            UniqueIndexEntity.Property.USER_EQUIPMENT,
                            equipmentEntity.getUserId() + "+" + equipmentEntity.getEquipmentId(),
                            entity.getKey()
                    );
                    */

                    ofy().put(equipmentEntity);
                }
            }
        }
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
