package cz.clovekvtisni.coordinator.server.service.impl;

import com.google.android.gcm.server.*;
import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.SecretInfoTemplate;
import cz.clovekvtisni.coordinator.domain.NotificationType;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.service.NotificationService;
import cz.clovekvtisni.coordinator.server.service.UserService;
import cz.clovekvtisni.coordinator.server.util.SecretInfoServerSide;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@Service("notificationService")
public class NotificationServiceImp extends AbstractServiceImpl implements NotificationService {

    @Autowired
    private UserService userService;

    private Sender sender;

    public NotificationServiceImp() {
        sender = new Sender(SecretInfoServerSide.GCM_API_KEY);
    }

    @Override
    public void sendPoiNotification(NotificationType type, PoiEntity poi, Long receiverUserId) {
        UserEntity receiver = userService.findById(receiverUserId, 0l);
	    logger.info("Sending push notification to "+receiver+" about "+poi);
        if (receiver == null || receiver.getPushTokensAndroid() == null || receiver.getPushTokensAndroid().size() == 0) {
	        logger.info("... user has not GCM tokens");
            return;
        }
        Message.Builder builder = new Message.Builder();

        // TODO odprasit tady ty stringy, pouzivaji se i v Androidu v cz.clovekvtisni.coordinator.android.GCMIntentService
        builder.addData("type", type.toString());
        builder.addData("eventId", poi.getEventId() != null ? poi.getEventId().toString() : null);
        builder.addData("poiId", poi.getId() != null ? poi.getId().toString() : null);
        builder.addData("latitude", poi.getLatitude() != null ? poi.getLatitude().toString() : null);
        builder.addData("longitude", poi.getLongitude() != null ? poi.getLongitude().toString() : null);
        builder.addData("name", poi.getName() != null ? poi.getName().toString() : null);
        builder.addData("organizationId", poi.getOrganizationId());

        Message message = builder.build();
        try {

            String[] pushes = receiver.getPushTokensAndroid().toArray(new String[0]);
            boolean changed = false;
            for (String push : pushes) {
                Result result = sender.send(message, push, 5);
                // FIXME: zpracovat vysledek
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
