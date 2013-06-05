package cz.clovekvtisni.coordinator.server;

import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AuthorizationTool;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 12.11.12
 */
public class AuthorizationToolTest extends AbstractTest {

    @Autowired
    AuthorizationTool authorizationTool;

    @Test
    public void testIsAuthorized() throws Exception {
        List<String> needRoles = new ArrayList<String>(2);

        // superadmin is always authorized
        Assert.assertTrue(authorizationTool.isAuthorized(
                Arrays.asList(new String[] {"EDITOR"}),
                Arrays.asList(new String[] {"SUPERADMIN"})
        ));

        // simple
        Assert.assertTrue(authorizationTool.isAuthorized(
                Arrays.asList(new String[] {"ADMIN"}),
                Arrays.asList(new String[] {"ADMIN"})
        ));

        // has authorized parent
        Assert.assertTrue(authorizationTool.isAuthorized(
                Arrays.asList(new String[] {"BACKEND"}),
                Arrays.asList(new String[] {"ADMIN"})
        ));
    }

    @Test
    public void testIsVisible() throws Exception {
        WorkflowState state = new WorkflowState();
        UserEntity user = new UserEntity();
        PoiEntity poi = new PoiEntity();
        poi.setWorkflowState(state);
    }
}
