package cz.clovekvtisni.coordinator.server.service;

import cz.clovekvtisni.coordinator.domain.NotificationType;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;

public interface NotificationService extends Service {

    void sendPoiNotification(NotificationType type, PoiEntity poi, Long receiverUserId);
}
