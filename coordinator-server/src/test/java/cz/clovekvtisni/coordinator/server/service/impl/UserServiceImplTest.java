package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.UserAuthKey;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 11:54 PM
 */
public class UserServiceImplTest extends LocalDatastoreTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreate() throws Exception {
        assertNotNull(userService);
        String testEmail = "foo@bar.cz";
        String[] testRoleIdList = new String[] {"a","b"};

        final User user = new User();
        user.setEmail(testEmail);
        user.setRoleIdList(testRoleIdList);

        UserEntity res = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserEntity>() {
            @Override
            public UserEntity run() {
                return userService.createUser(new UserEntity().populateFrom(user));
            }
        });
        assertNotNull(res.getId());
        assertNotNull(res.getRoleIdList());
        assertArrayEquals(testRoleIdList, res.getRoleIdList());
    }

    @Test
    public void testFindByFilter() throws Exception {
        assertNotNull(userService);

        UserEntity byId = userService.findById(1l, 0l);

        UserFilter filter = new UserFilter();
        filter.setEmailVal(System.getProperty("default.admin.email", "admin@m-atelier.cz"));
        ResultList<UserEntity> resultList = userService.findByFilter(filter, 2, null, 0l);
        assertNotNull(resultList.getResult());
        assertEquals(1, resultList.getResultSize());
    }

    @Test
    public void testAuthKey() {
        UserEntity user = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserEntity>() {
            @Override
            public UserEntity run() {
                UserEntity user = new UserEntity();
                user.setEmail("foo@bar.cz");
                return userService.createUser(user);
            }
        });
        UserAuthKey authKey = userService.createAuthKey(user);
        assertNotNull(authKey);
        UserEntity loaded = userService.getByAuthKey(authKey.getAuthKey());
        assertEquals(user, loaded);
        assertEquals(user.getEmail(), loaded.getEmail());
    }

    @Test
    public void testPreRegister() {
        UserEntity user = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserEntity>() {
            @Override
            public UserEntity run() {
                UserEntity user = new UserEntity();
                for (Organization organization : config.getOrganizationList()) {
                    if (organization.isAllowsPreRegistration()) {
                        user.setOrganizationId(organization.getId());
                        return userService.preRegister(user);
                    }
                }
                return null;
            }
        });
        assertNotNull(user);
        assertNotNull(user.getId());
    }

    @Test
    public void testRegister() {
        UserInEventEntity res = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserInEventEntity>() {
            @Override
            public UserInEventEntity run() {
                UserEntity user = new UserEntity();
                user.setOrganizationId("org1");
                UserInEventEntity inEvent = new UserInEventEntity();
                return userService.register(user, inEvent);
            }
        });
        assertNotNull(res);
        assertNotNull(res.getId());
    }
}
