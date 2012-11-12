package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.domain.config.Equipment;
import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.service.EquipmentService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class EquipmentServiceImplTest extends LocalDatastoreTest {

    @Autowired
    private CoordinatorConfig config;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private UserService userService;

    @Test
    public void testAddUserEquipment() throws Exception {
        Equipment testEquipment = config.getEquipmentList().get(0);
        UserEntity user = findAdminUser();
        final UserEquipment equipment = new UserEquipment();
        equipment.setUserId(user.getId());
        equipment.setEquipmentId(testEquipment.getId());

        UserEquipmentEntity res = securityTool.runWithDisabledSecurity(new RunnableWithResult<UserEquipmentEntity>() {
            @Override
            public UserEquipmentEntity run() {
                return equipmentService.addUserEquipment(new UserEquipmentEntity().populateFrom(equipment));
            }
        });
        Assert.assertNotNull(res.getId());
        Assert.assertEquals(testEquipment.getId(), res.getEquipmentId());
        Assert.assertEquals(user.getId(), res.getUserId());
    }

    private UserEntity findAdminUser() {
        UserFilter filter = new UserFilter();
        filter.setEmailVal(System.getProperty("default.admin.email", "admin@m-atelier.cz"));
        return userService.findByFilter(filter, 1, null).singleResult();
    }
}
