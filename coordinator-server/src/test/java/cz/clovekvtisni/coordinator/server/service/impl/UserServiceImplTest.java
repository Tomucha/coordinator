package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        user.setRoleIdList(Arrays.asList(testRoleIdList));
        user.setNewPassword("aaa");

        UserEntity res = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserEntity>() {
            @Override
            public UserEntity run() {
                return userService.createUser(new UserEntity().populateFrom(user));
            }
        });
        assertNotNull(res.getId());
        assertNotNull(res.getRoleIdList());
        assertArrayEquals(testRoleIdList, res.getRoleIdList().toArray());
    }

    @Test
    public void testFindByFilter() throws Exception {
        assertNotNull(userService);

        UserEntity byId = userService.findById(1l);

        UserFilter filter = new UserFilter();
        filter.setEmailVal(System.getProperty("default.admin.email", "admin@m-atelier.cz"));
        ResultList<UserEntity> resultList = userService.findByFilter(filter, 2, null);
        assertNotNull(resultList.getResult());
        assertEquals(1, resultList.getResultSize());
    }
}
