package cz.clovekvtisni.coordinator.server.workflow.callback;

import cz.clovekvtisni.coordinator.domain.config.WorkflowTransition;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.service.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LastUserClosesDoorCallback extends WorkflowCallback {

    @Autowired
    private PoiService poiService;

    @Override
    public boolean onBeforeTransition(PoiEntity poiEntity, WorkflowTransition transition) {
        poiEntity = poiService.unassignUser(poiEntity, getLoggedUser().getId());
        return  poiEntity.getUserCount() == 0;
    }
}
