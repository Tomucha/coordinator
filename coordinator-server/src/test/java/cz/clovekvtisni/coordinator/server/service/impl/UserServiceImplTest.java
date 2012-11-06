package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import cz.clovekvtisni.coordinator.server.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

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

        User user = new User();
        user.setEmail(testEmail);
        user.setRoleIdList(Arrays.asList(testRoleIdList));
        user.setNewPassword("aaa");

        User res = userService.createUser(user);
        assertNotNull(res.getId());
        assertNotNull(res.getRoleIdList());
        assertArrayEquals(testRoleIdList, res.getRoleIdList().toArray());
    }

    @Test
    public void testFindByFilter() throws Exception {
        assertNotNull(userService);

        User byId = userService.findById(1l);

        UserFilter filter = new UserFilter();
        filter.setEmail("admin@test");
        ResultList<User> resultList = userService.findByFilter(filter, 2, null);
        assertNotNull(resultList.getResult());
        assertEquals(1, resultList.getResultSize());
    }
}
