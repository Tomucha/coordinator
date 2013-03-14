package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationFilter;
import cz.clovekvtisni.coordinator.server.filter.OrganizationInEventFilter;
import cz.clovekvtisni.coordinator.server.service.OrganizationInEventService;
import cz.clovekvtisni.coordinator.server.service.OrganizationService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        populate(Arrays.asList(new OrganizationInEventEntity[] {inEvent}), flags);

        return inEvent;
    }

    @Override
    public ResultList<OrganizationInEventEntity> findByFilter(OrganizationInEventFilter filter, int limit, String bookmark, long flags) {
        ResultList<OrganizationInEventEntity> result = ofy().findByFilter(filter, bookmark, limit);

        populate(result.getResult(), flags);

        return result;
    }

    private void populate(List<OrganizationInEventEntity> result, long flags) {
        if ((flags & FLAG_FETCH_EVENT) != 0) {
            Map<Key<EventEntity>, OrganizationInEventEntity> inEventMap = new HashMap<Key<EventEntity>, OrganizationInEventEntity>(result.size());
            for (OrganizationInEventEntity inEvent : result) {
                Key<EventEntity> key = Key.create(EventEntity.class, inEvent.getEventId());
                inEventMap.put(key, inEvent);
            }
            Map<Key<EventEntity>, EventEntity> entityMap = ofy().get(inEventMap.keySet());
            for (Map.Entry<Key<EventEntity>, EventEntity> entry : entityMap.entrySet()) {
                if (entry.getValue().isDeleted()) continue;
                inEventMap.get(entry.getKey()).setEventEntity(entry.getValue());
            }
        }

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

    @Override
    public OrganizationInEventEntity findEventInOrganization(Long eventId, String organizationId, long flags) {
        if (eventId == null || organizationId == null)
            return null;
        OrganizationInEventFilter inEventFilter = new OrganizationInEventFilter();
        inEventFilter.setOrganizationIdVal(organizationId);
        inEventFilter.setEventIdVal(eventId);
        ResultList<OrganizationInEventEntity> result = findByFilter(inEventFilter, 1, null, 0l);

        return  result.firstResult();
    }
}
