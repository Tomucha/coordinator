package cz.clovekvtisni.coordinator.server.service.impl;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.server.LocalDatastoreTest;
import cz.clovekvtisni.coordinator.server.domain.EventEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.server.service.EventService;
import cz.clovekvtisni.coordinator.util.RunnableWithResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

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
        final String testDescription = "foo";
        final String testEventId = "bar";
        event.setDescription(testDescription);
        event.setEventId(testEventId);
        securityTool.runWithDisabledSecurity(new RunnableWithResult<Object>() {
            @Override
            public Object run() {
                EventEntity res = eventService.createEvent(event);
                assertNotNull(res);
                assertEquals(testDescription, res.getDescription());
                assertEquals(testEventId, res.getEventId());
                return null;
            }
        });
    }
}
