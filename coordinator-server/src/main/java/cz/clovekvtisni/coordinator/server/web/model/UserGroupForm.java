package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.12.12
 */
public class UserGroupForm extends UserGroupEntity {

    private String retUrl;

    public String getRetUrl() {
        return retUrl;
    }

    public void setRetUrl(String retUrl) {
        this.retUrl = retUrl;
    }
}
