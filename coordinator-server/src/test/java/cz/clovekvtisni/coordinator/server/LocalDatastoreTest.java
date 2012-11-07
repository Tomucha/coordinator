package cz.clovekvtisni.coordinator.server;

import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.service.SystemService;
import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Performs datastore setup, as described <a
 * href="http://code.google.com/appengine/docs/java/howto/unittesting.html">here</a>.
 *
 * @author androns
 */
public abstract class LocalDatastoreTest extends AbstractTest {

    private final LocalServiceTestHelper helper =
        new LocalServiceTestHelper(
                new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(20)
        );

    @Autowired
    protected SystemService systemService;

    @Autowired
    protected SecurityTool securityTool;

    /**
     *
     */
    @Before
    public void setUp() {
        this.helper.setUp();
        systemService.initApplication();
    }

    /**
     * @see LocalServiceTest#tearDown()
     */
    @After
    public void tearDown() {
        this.helper.tearDown();
    }
}
