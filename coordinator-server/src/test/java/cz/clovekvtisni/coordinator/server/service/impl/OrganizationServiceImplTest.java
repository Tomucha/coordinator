package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.server.AbstractTest;
import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.service.OrganizationService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class OrganizationServiceImplTest extends AbstractTest {

    @Autowired
    private OrganizationService organizationService;

    @Test
    public void testFindById() throws Exception {
        Organization org1 = organizationService.findById("org1");
        assertNotNull(org1);
    }
}
