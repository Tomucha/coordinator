package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.EventLocationEntity;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/27/12
 * Time: 11:54 PM
 */
public class EventServiceImplTest extends LocalDatastoreTest {

    @Autowired
    private EventService eventService;

    @Test
    public void testCreate() throws Exception {
        assertNotNull(eventService);
        final EventEntity event = new EventEntity();
        final String testEventId = "event.test." + System.currentTimeMillis();
        final String testDescription = "foo";
        List<EventLocationEntity> locationList = new ArrayList<EventLocationEntity>(1);
        EventLocationEntity locationEntity = new EventLocationEntity();
        locationEntity.setLatitude(1d);
        locationEntity.setLongitude(2d);
        locationEntity.setRadius(3l);
        locationList.add(locationEntity);

        event.setEventId(testEventId);
        event.setDescription(testDescription);
        event.setEventLocationList(locationList.toArray(new EventLocationEntity[0]));

        securityTool.runWithDisabledSecurity(new RunnableWithResult<Object>() {
            @Override
            public Object run() {
                EventEntity res = eventService.createEvent(event);
                res = eventService.findByEventId(res.getEventId(), EventService.FLAG_FETCH_LOCATIONS);
                assertNotNull(res);
                assertEquals(testDescription, res.getDescription());
                assertEquals(testEventId, res.getEventId());
                assertNotNull(res.getEventLocationList());
                assertEquals(1, res.getEventLocationList().length);
                return null;
            }
        });
    }
}
