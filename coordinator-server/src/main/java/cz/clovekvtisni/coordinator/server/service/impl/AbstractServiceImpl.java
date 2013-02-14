package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.appengine.api.datastore.ReadPolicy;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import cz.clovekvtisni.coordinator.server.domain.AbstractPersistentEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.AppContext;
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

    @Autowired
    protected AppContext appContext;

    @Autowired
    public void setObjectifyFactory(ObjectifyFactory objectifyFactory) {
        ObjectifyService.setFactory(objectifyFactory);
    }

    protected MaObjectify ofy() {
        // TODO maybe better than creating new instance is subclassing ObjectifyService
        return new MaObjectify(ObjectifyService.ofy());
    }

    protected void updateSystemFields(AbstractPersistentEntity entity, AbstractPersistentEntity old) {
        Date now = new Date();
        if (entity.isNew()) {
            entity.setCreatedDate(now);
        } else if (old != null) {
            entity.setCreatedDate(old.getCreatedDate());
        }
        entity.setModifiedDate(now);
    }

    protected UserEntity getLoggedUser() {
        return appContext.getLoggedUser();
    }
}
