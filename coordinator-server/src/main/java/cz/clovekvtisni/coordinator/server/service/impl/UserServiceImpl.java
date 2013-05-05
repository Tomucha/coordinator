package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.*;
import com.google.appengine.api.taskqueue.Queue;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.exception.MaPermissionDeniedException;
import cz.clovekvtisni.coordinator.exception.ValidationError;
import cz.clovekvtisni.coordinator.server.domain.*;
import cz.clovekvtisni.coordinator.server.filter.UserEquipmentFilter;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.filter.UserSkillFilter;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import cz.clovekvtisni.coordinator.server.service.*;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.util.CloneTool;
import cz.clovekvtisni.coordinator.util.SignatureTool;
import cz.clovekvtisni.coordinator.util.ValueTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:25 PM
 */
@Service("userService")
public class UserServiceImpl extends AbstractEntityServiceImpl implements UserService {

    private static final String PASSWORD_SEED = "e{\"DFGP:2354\":asdlghH%$~23'5;'";
    private static final long LIMIT_MILLIS = 1000 * 25;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserInEventService userInEventService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrganizationInEventService organizationInEventService;

    @Autowired
    private AuthorizationTool authorizationTool;

    @Override
    public UserEntity login(String email, String password, String... hasRoles) {
        Key<UserEntity> userKey = systemService.findUniqueValueOwner(ofy(), UniqueIndexEntity.Property.EMAIL, ValueTool.normalizeEmail(email));
        if (userKey == null) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        UserEntity userEntity = ofy().load().key(userKey).get();
        if (userEntity == null || password == null || !passwordHash(userEntity.getId(), password).equals(userEntity.getPassword())) {
            throw MaPermissionDeniedException.wrongCredentials();
        }

        if (hasRoles != null && hasRoles.length > 0) {
            boolean isPermited = false;
            for (String hasRole : hasRoles) {
                if (authorizationTool.hasRole(hasRole, userEntity)) {
                    isPermited = true;
                    break;
                }
            }
            if (!isPermited)
                throw MaPermissionDeniedException.permissionDenied();
        }

        appContext.setLoggedUser(userEntity);

        return userEntity;
    }

    @Override
    public UserEntity findById(Long id, long flags) {
        UserEntity userEntity = ofy().load().key(Key.create(UserEntity.class, id)).get();

        if (userEntity != null)
            populate(Arrays.asList(new UserEntity[]{userEntity}), flags);

        return userEntity;

    }

    private void populate(Collection<UserEntity> entities, long flags) {
        for (UserEntity userEntity : entities) {
            if ((flags & FLAG_FETCH_EQUIPMENT) != 0) {
                UserEquipmentFilter filter = new UserEquipmentFilter();
                filter.setUserIdVal(userEntity.getId());
                ResultList<UserEquipmentEntity> equipments = ofy().findByFilter(filter, null, 0);
                if (equipments.getResultSize() > 0) {
                    userEntity.setEquipmentEntityList(equipments.getResult().toArray(new UserEquipmentEntity[0]));
                } else {
                    userEntity.setEquipmentEntityList(new UserEquipmentEntity[0]);
                }
            }
            if ((flags & FLAG_FETCH_SKILLS) != 0) {
                UserSkillFilter filter = new UserSkillFilter();
                filter.setUserIdVal(userEntity.getId());
                ResultList<UserSkillEntity> skills = ofy().findByFilter(filter, null, 0);
                if (skills.getResultSize() > 0) {
                    userEntity.setSkillEntityList(skills.getResult().toArray(new UserSkillEntity[0]));
                } else {
                    userEntity.setSkillEntityList(new UserSkillEntity[0]);
                }
            }
        }
    }

    @Override
    public ResultList<UserEntity> findByFilter(UserFilter filter, int limit, String bookmark, long flags) {
        if (filter.getOrder() == null)
            filter.setOrder("id");
        return ofy().findByFilter(filter, bookmark, limit);
    }

    private String passwordHash(Long userId, String password) {
        String passwd = SignatureTool.md5Digest(userId + "~" + PASSWORD_SEED + "~" + password);

        return passwd;
    }

    @Override
    public UserEntity createUser(final UserEntity entity) {
        return ofy().transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                logger.debug("creating " + entity);

                // TODO validace roli

                // TODO maybe more IQ solution
                if (ValueTool.isEmpty(entity.getEmail()))
                    throw ValidationError.entityInvalid();

                entity.setId(null);
                entity.setEmail(ValueTool.normalizeEmail(entity.getEmail()));
                updateSystemFields(entity, null);
                ofy().put(entity);

                entity.setPassword(passwordHash(entity.getId(), entity.getPassword()));
                ofy().put(entity);

                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, entity.getEmail(), entity.getKey());

                saveFields(entity, null);

                return entity;
            }
        });
    }

    @Override
    public UserEntity updateUser(final UserEntity updated) {
        final UserEntity user = CloneTool.deepClone(updated);
        final UserEntity old = findById(user.getId(), UserService.FLAG_FETCH_EQUIPMENT | UserService.FLAG_FETCH_SKILLS);
        user.setOrganizationId(old.getOrganizationId());

        if (authorizationTool.hasRole(AuthorizationTool.SUPERADMIN, getLoggedUser())) {
            // superadmin is allowed to change organization
            if (updated.getOrganizationId() != null) {
                user.setOrganizationId(updated.getOrganizationId());
            }
        }

        logger.debug("updating " + user);
        return ofy().transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                // TODO co kdyz appengina nic nevrati? vyhodit vyjimku?
                // only special method is able to change password
                // FIXME: a zase - nacteni a update musi byt v transakci
                user.setPassword(old.getPassword());
                updateSystemFields(user, old);
                systemService.deleteUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, old.getEmail());
                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, user.getEmail(), user.getKey());

                user.setEmail(ValueTool.normalizeEmail(user.getEmail()));
                UserEntity created = ofy().put(user);

                saveFields(created, old);

                return created;
            }
        });
    }

    @Override
    public UserEntity createUserInEvent(final UserEntity user, final UserInEventEntity inEventEntity) {
        return ofy().transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                user.setId(null);
                if (user.getRoleIdList() == null)
                    user.setRoleIdList(new String[]{AuthorizationTool.ANONYMOUS});
                UserEntity created = createUser(user);

                inEventEntity.setParentKey(user.getKey());
                inEventEntity.setUserId(created.getId());
                inEventEntity.setUserEntity(created);
                userInEventService.create(inEventEntity);

                return created;
            }
        });
    }

    @Override
    public UserEntity updateUserInEvent(final UserEntity newUser, final UserInEventEntity inEventEntity) {
        final UserEntity oldUser = findById(newUser.getId(), UserService.FLAG_FETCH_EQUIPMENT | UserService.FLAG_FETCH_SKILLS);
        final UserEntity copy = CloneTool.deepClone(oldUser);
        final UserInEventEntity oldInEventEntity = userInEventService.findById(inEventEntity.getEventId(), newUser.getId(), 0l);

        return ofy().transact(new Work<UserEntity>() {
            @Override
            public UserEntity run() {
                copy.setFirstName(newUser.getFirstName());
                copy.setLastName(newUser.getLastName());
                copy.setPhone(newUser.getPhone());
                copy.setEmail(ValueTool.normalizeEmail(newUser.getEmail()));
                copy.setBirthday(newUser.getBirthday());
                copy.setAddressLine(newUser.getAddressLine());
                copy.setCity(newUser.getCity());
                copy.setZip(newUser.getZip());
                copy.setCountry(newUser.getCountry());
                copy.setModifiedDate(new Date());

                systemService.deleteUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, copy.getEmail());
                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.EMAIL, copy.getEmail(), copy.getKey());

                UserEntity updated = ofy().put(copy);
                updated.setEquipmentEntityList(newUser.getEquipmentEntityList());
                updated.setSkillEntityList(newUser.getSkillEntityList());
                saveFields(updated, oldUser);

                if (oldInEventEntity != null) {
                    oldInEventEntity.setGroupIdList(inEventEntity.getGroupIdList());
                    oldInEventEntity.setLastLocationLatitude(inEventEntity.getLastLocationLatitude());
                    oldInEventEntity.setLastLocationLongitude(inEventEntity.getLastLocationLongitude());
                    userInEventService.update(oldInEventEntity);

                } else {
                    inEventEntity.setUserId(updated.getId());
                    inEventEntity.setParentKey(Key.create(UserEntity.class, updated.getId()));
                    inEventEntity.setUserEntity(updated);
                    userInEventService.create(inEventEntity);
                }


                return updated;
            }
        });

    }

    /**
     * updated "verify" fields is ignored here
     */
    private void saveFields(UserEntity entity, UserEntity old) {
        UserEquipmentEntity[] newEquipmentList = entity.getEquipmentEntityList();
        if (newEquipmentList != null) {
            Map<String, UserEquipmentEntity> oldEquipmentMap = old != null ? old.getEquipmentEntityMap() : new HashMap<String, UserEquipmentEntity>();
            List<UserEquipmentEntity> saved = new ArrayList<UserEquipmentEntity>();
            Map<String, UserEquipmentEntity> toSave = new HashMap<String, UserEquipmentEntity>();

            for (UserEquipmentEntity equipmentEntity : newEquipmentList) {
                if (equipmentEntity.isDeleted())
                    continue;
                else if (oldEquipmentMap.containsKey(equipmentEntity.getEquipmentId())) {
                    saved.add(oldEquipmentMap.get(equipmentEntity.getEquipmentId()));
                    oldEquipmentMap.remove(equipmentEntity.getEquipmentId());

                } else {
                    equipmentEntity.setUserId(entity.getId());
                    equipmentEntity.setParentKey(entity.getKey());
                    updateSystemFields(equipmentEntity, null);
                    toSave.put(equipmentEntity.getEquipmentId(), equipmentEntity);
                    saved.add(equipmentEntity);
                }
            }

            for (UserEquipmentEntity equipmentEntity : toSave.values()) {
                ofy().put(equipmentEntity);
            }
            for (UserEquipmentEntity equipmentEntity : oldEquipmentMap.values()) {
                ofy().delete(equipmentEntity);
            }

            entity.setEquipmentEntityList(saved.toArray(new UserEquipmentEntity[0]));
        }

        UserSkillEntity[] newSkillList = entity.getSkillEntityList();
        if (newSkillList != null) {
            Map<String, UserSkillEntity> oldSkillMap = old != null ? old.getSkillEntityMap() : new HashMap<String, UserSkillEntity>();
            List<UserSkillEntity> saved = new ArrayList<UserSkillEntity>();
            Map<String, UserSkillEntity> toSave = new HashMap<String, UserSkillEntity>();

            for (UserSkillEntity skillEntity : newSkillList) {
                if (skillEntity.isDeleted())
                    continue;
                else if (oldSkillMap.containsKey(skillEntity.getSkillId())) {
                    saved.add(oldSkillMap.get(skillEntity.getSkillId()));
                    oldSkillMap.remove(skillEntity.getSkillId());

                } else {
                    skillEntity.setUserId(entity.getId());
                    skillEntity.setParentKey(entity.getKey());
                    updateSystemFields(skillEntity, null);
                    toSave.put(skillEntity.getSkillId(), skillEntity);
                    saved.add(skillEntity);
                }
            }

            for (UserSkillEntity skillEntity : toSave.values()) {
                ofy().put(skillEntity);
            }
            for (UserSkillEntity skillEntity : oldSkillMap.values()) {
                ofy().delete(skillEntity);
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

    @Override
    public void emailAllUsers(String subject, String htmlBody, String queryToken) {

        long startTime = System.currentTimeMillis();

        Query<UserEntity> query = ofy().load().type(UserEntity.class);
        query = query.order("email");

        if (queryToken != null) {
            Cursor cursor = Cursor.fromWebSafeString(queryToken);
            query = query.startAt(cursor);
        }

        QueryResultIterator<UserEntity> result = query.iterator();

        while (result.hasNext()) {
            UserEntity userEntity = result.next();
            if (!userEntity.isUnsubscribed() && userEntity.getEmail().toLowerCase().contains("tomucha.cz")) {
                logger.info("Sending email to " + userEntity.getEmail());
                emailService.sendEmail(userEntity.getEmail(), subject, htmlBody);
            }

            if (System.currentTimeMillis() - startTime > LIMIT_MILLIS) {
                Cursor cursor = result.getCursor();
                logger.info("Adding cursor to queue: "+cursor.toWebSafeString());
                Queue queue = QueueFactory.getQueue("mass-mail-queue");

                TaskOptions task = TaskOptions.Builder.withUrl("/tools/massMailContinue");
                task = task.param("cursor", cursor.toWebSafeString());
                task = task.param("subject", subject);
                task = task.param("htmlBody", htmlBody);
                task = task.method(TaskOptions.Method.POST);

                queue.add(task);
                break;
            }
        }
    }

    @Override
    public UserAuthKey createAuthKey(UserEntity user) {
        String random = SignatureTool.md5Digest(user.getId() + user.getEmail() + Math.random());
        UserAuthKey authKey = new UserAuthKey();
        authKey.setAuthKey(random);
        authKey.setUser(user);

        UserAuthKey saved = ofy().put(authKey);

        return saved;
    }

    @Override
    public UserEntity getByAuthKey(String key) {
        UserAuthKey authKey = ofy().get(Key.create(UserAuthKey.class, key));
        if (authKey == null)
            return null;
        return authKey.getUser();
    }

    @Override
    public UserEntity preRegister(UserEntity newUser, long flags, boolean systemCall) {
        Organization organization = organizationService.findById(newUser.getOrganizationId(), 0l);

        if (organization == null)
            throw new IllegalArgumentException("Not existed organization id in " + newUser);

        if (!systemCall) {
            if (!organization.isAllowsPreRegistration())
                throw MaPermissionDeniedException.registrationNotAllowed();
        }

        return createUser(newUser);
    }

    @Override
    public UserInEventEntity register(final UserEntity user, final UserInEventEntity inEvent, long flags) {
        Organization organization = organizationService.findById(user.getOrganizationId(), 0l);
        boolean isForceRegistration = (flags & FLAG_FORCE_REGISTRATION) != 0;

        if (organization == null)
            throw new IllegalArgumentException("Not existed organization id in " + user);

        if (!isForceRegistration && !organization.isAllowsRegistration())
            throw MaPermissionDeniedException.registrationNotAllowed();

        OrganizationInEventEntity info = organizationInEventService.findEventInOrganization(inEvent.getEventId(), organization.getId(), 0l);

        if (!isForceRegistration && (info == null
                || (info.getDateClosedRegistration() != null && info.getDateClosedRegistration().compareTo(new Date()) < 0)
                || !user.getOrganizationId().equals(info.getOrganizationId())
        )) {
            throw MaPermissionDeniedException.registrationNotAllowed();
        }

        return ofy().transact(new Work<UserInEventEntity>() {
            @Override
            public UserInEventEntity run() {
                // FIXME: tohle je taky zhovadily: user a userinevent maji vzniknout v jedny transakci,
                // pokud uz existuji, meli by se rozumne updatnout

                UserEntity connectedUser = user.isNew() ? createUser(user) : user;

                inEvent.setParentKey(connectedUser.getKey());
                inEvent.setUserId(connectedUser.getId());
                inEvent.setUserEntity(connectedUser);

                UserInEventEntity resultEvent = userInEventService.create(inEvent);
                systemService.saveUniqueIndexOwner(ofy(), UniqueIndexEntity.Property.USER_IN_EVENT, resultEvent.getUserId() + "~" + resultEvent.getEventId(), resultEvent.getKey());

                resultEvent.setUserEntity(connectedUser);

                return resultEvent;
            }
        });
    }

    @Override
    public void registerPushTokenAndroid(final String token) {
        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                UserEntity user = getLoggedUser();
                user = ofy().load().key(user.getKey()).get();
                user.getPushTokensAndroid().add(token);
                ofy().save().entity(user).now();
            }
        });

    }

    @Override
    public boolean unsubscribe(String email, String signature) {
        // FIXME: unsubscribe

        return false;
    }

    @Override
    public List<UserEntity> findByIds(long flags, Long... ids) {
        if (ids == null)
            return null;

        Set<Key<UserEntity>> keys = new HashSet<Key<UserEntity>>(ids.length);
        for (Long id : ids) {
            if (id != null)
                keys.add(Key.create(UserEntity.class, id));
        }

        Map<Key<UserEntity>, UserEntity> entityMap = ofy().get(keys);
        populate(entityMap.values(), flags);

        return new ArrayList<UserEntity>(entityMap.values());
    }

    @Override
    public UserEntity suspendUser(Long id, String reason, long flags) {
        UserEntity user = findById(id, 0l);
        if (user == null || user.getDateSuspended() != null)
            return user;
        user.setDateSuspended(new Date());
        user.setReasonSuspended(reason);
        return updateUser(user);
    }
}
