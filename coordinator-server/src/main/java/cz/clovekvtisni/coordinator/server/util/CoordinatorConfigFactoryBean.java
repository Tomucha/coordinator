package cz.clovekvtisni.coordinator.server.util;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class CoordinatorConfigFactoryBean implements FactoryBean<CoordinatorConfig> {

    private String configFilePath;

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @Override
    public CoordinatorConfig getObject() throws Exception {
        Serializer xml = new Persister();
        ClassPathResource xmlFile = new ClassPathResource(configFilePath);
        System.out.println("config file: " + xmlFile.getPath());
        CoordinatorConfig config = xml.read(CoordinatorConfig.class, xmlFile.getInputStream());

        return config;
    }

    @Override
    public Class<?> getObjectType() {
        return CoordinatorConfig.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
