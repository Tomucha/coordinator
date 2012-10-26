package cz.clovekvtisni.coordinator.server.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public final class ContextInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
//        ObjectifyService.register(Message.class);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        // empty
    }
}
