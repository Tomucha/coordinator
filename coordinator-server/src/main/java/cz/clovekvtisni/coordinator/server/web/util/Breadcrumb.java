package cz.clovekvtisni.coordinator.server.web.util;

import cz.clovekvtisni.coordinator.server.domain.CoordinatorEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 19.11.12
 */
public class Breadcrumb {

    private String url;

    private String labelCode;

    private String[] roles;

    private CoordinatorEntity entity;

    public Breadcrumb(String url, String labelCode, String... roles) {
        this.url = url;
        this.labelCode = labelCode;
        this.roles = roles;
    }

    public Breadcrumb(CoordinatorEntity entity, String url, String labelCode, String... roles) {
        this.entity = entity;
        this.url = url;
        this.labelCode = labelCode;
        this.roles = roles;
    }

    public String getUrl() {
        return url;
    }

    public String getLinkUrl() {
        String url = this.url;
        if (entity != null) {
            url = url + "?id=" + entity.getId();
        }

        return url;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public List<String> isVisibleFor() {
        return roles != null ? Arrays.asList(roles) : new ArrayList<String>(0);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public void setEntity(CoordinatorEntity entity) {
        this.entity = entity;
    }
}
