package cz.clovekvtisni.coordinator.server;

import cz.clovekvtisni.coordinator.server.security.AppContext;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 11:53 PM
 */
@ContextConfiguration(locations = {"classpath:applicationContext-test.xml"})
public abstract class AbstractTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    protected AppContext appContext;

    @Before
    public void prepareAppContext() {
        System.setProperty("default.admin.login", "admin");
        System.setProperty("default.admin.password", "admin");
        System.setProperty("default.admin.email", "admin@test");
        appContext.setLocale(Locale.ENGLISH);
    }

    @After
    public void cleanAppContext() {
        appContext.setLocale(null);
        appContext.setLoggedUser(null);
    }
}
