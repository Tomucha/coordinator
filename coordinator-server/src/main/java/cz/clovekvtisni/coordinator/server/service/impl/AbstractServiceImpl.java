package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.ObjectifyFactory;
import cz.clovekvtisni.coordinator.server.domain.AbstractPersistentEntity;
import cz.clovekvtisni.coordinator.server.service.Service;
import cz.clovekvtisni.coordinator.server.tool.objectify.MaObjectify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:25 PM
 */
public class AbstractServiceImpl implements Service {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private ObjectifyFactory objectifyFactory;

    @Autowired
    public void setObjectifyFactory(ObjectifyFactory objectifyFactory) {
        this.objectifyFactory = objectifyFactory;
    }

    protected MaObjectify ofy() {
        return new MaObjectify(objectifyFactory.begin());
    }

    protected void updateSystemFields(AbstractPersistentEntity entity) {
        Date now = new Date();
        if (entity.isNew())
            entity.setCreatedDate(now);
        entity.setModifiedDate(now);
    }
}
