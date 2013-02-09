package cz.clovekvtisni.coordinator.server.web.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 9.12.12
 */
public class PoiMultiSelection {

    private Long eventId;

    private List<Long> selectedPois;

    private SelectedPoiAction selectedAction;

    public List<Long> getSelectedPois() {
        return selectedPois;
    }

    public void setSelectedPois(List<Long> selectedPois) {
        this.selectedPois = selectedPois;
    }

    public SelectedPoiAction getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(SelectedPoiAction selectedAction) {
        this.selectedAction = selectedAction;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
