package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import cz.clovekvtisni.coordinator.server.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    public void testFindByFilter() throws Exception {
        assertNotNull(userService);

        UserEntity byId = userService.findById(1l);

        UserFilter filter = new UserFilter();
        filter.setLogin("admin");
        ResultList<UserEntity> resultList = userService.findByFilter(filter, 2, null);
        assertNotNull(resultList.getResult());
        assertEquals(1, resultList.getResultSize());
    }
}
