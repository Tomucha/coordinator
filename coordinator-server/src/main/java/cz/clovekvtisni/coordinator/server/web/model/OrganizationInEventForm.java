package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 20.11.12
 */
public class OrganizationInEventForm extends OrganizationInEventEntity {

    private String retUrl;

    public String getRetUrl() {
        return retUrl;
    }

    public void setRetUrl(String retUrl) {
        this.retUrl = retUrl;
    }
}
