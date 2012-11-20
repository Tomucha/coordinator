package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("organizationInEventService")
public class OrganizationInEventServiceImpl extends AbstractEntityServiceImpl implements OrganizationInEventService {

    @Override
    public OrganizationInEventEntity findById(Long id, long flags) {
        OrganizationInEventEntity inEvent = ofy().get(Key.create(OrganizationInEventEntity.class, id));

        return inEvent;
    }

    @Override
    public ResultList<OrganizationInEventEntity> findByFilter(OrganizationInEventFilter filter, int limit, String bookmark, long flags) {
        return ofy().findByFilter(filter, bookmark, limit);
    }

    @Override
    public OrganizationInEventEntity create(final OrganizationInEventEntity inEvent) {
        logger.debug("creating " + inEvent);
        return ofy().transact(new Work<OrganizationInEventEntity>() {
            @Override
            public OrganizationInEventEntity run() {
                inEvent.setId(null);
                updateSystemFields(inEvent, null);

                ofy().put(inEvent);

                return inEvent;
            }
        });
    }

    @Override
    public OrganizationInEventEntity update(final OrganizationInEventEntity inEvent) {
        logger.debug("updating " + inEvent);
        final OrganizationInEventEntity old = findById(inEvent.getId(), 0l);
        if (old == null) throw NotFoundException.idNotExist(OrganizationInEvent.class.getSimpleName(), inEvent.getId());
        return ofy().transact(new Work<OrganizationInEventEntity>() {
            @Override
            public OrganizationInEventEntity run() {
                updateSystemFields(inEvent, old);

                ofy().put(inEvent);

                return inEvent;
            }
        });
    }
}
