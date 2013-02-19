package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.Poi;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventPoiResponseData implements ApiResponseData {

    private Poi poi;

    public EventPoiResponseData() {
    }

    public EventPoiResponseData(Poi poi) {
        this.poi = poi;
    }

    public Poi getPoi() {
        return poi;
    }

    public void setPoi(Poi poi) {
        this.poi = poi;
    }
}
