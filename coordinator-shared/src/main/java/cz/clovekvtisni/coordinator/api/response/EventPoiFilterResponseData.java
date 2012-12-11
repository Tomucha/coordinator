package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.Poi;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 11.12.12
 */
public class EventPoiFilterResponseData implements ApiResponseData {

    private Poi[] pois;

    public EventPoiFilterResponseData() {
    }

    public EventPoiFilterResponseData(Poi[] pois) {
        this.pois = pois;
    }

    public EventPoiFilterResponseData(List<Poi> pois) {
        this.pois = pois.toArray(new Poi[0]);
    }

    public Poi[] getPois() {
        return pois;
    }

    public void setPois(Poi[] pois) {
        this.pois = pois;
    }
}
