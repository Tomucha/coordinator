package cz.clovekvtisni.coordinator.domain;

import java.util.Arrays;

public class Poi extends AbstractModifiableEntity {

    private Long poiCategoryId;

    private Long workflowId;

    private Long workflowStateId;

    private Long[] userId;

    private Double latitude;

    private Double longitude;

    private Long precission;

    private boolean confirmed;

    public Long getPoiCategoryId() {
        return poiCategoryId;
    }

    public void setPoiCategoryId(Long poiCategoryId) {
        this.poiCategoryId = poiCategoryId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Long getWorkflowStateId() {
        return workflowStateId;
    }

    public void setWorkflowStateId(Long workflowStateId) {
        this.workflowStateId = workflowStateId;
    }

    public Long[] getUserId() {
        return userId;
    }

    public void setUserId(Long[] userId) {
        this.userId = userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getPrecission() {
        return precission;
    }

    public void setPrecission(Long precission) {
        this.precission = precission;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "Poi{" +
                "poiCategoryId=" + poiCategoryId +
                ", workflowId=" + workflowId +
                ", workflowStateId=" + workflowStateId +
                ", userId=" + (userId == null ? null : Arrays.asList(userId)) +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", precission=" + precission +
                ", confirmed=" + confirmed +
                '}';
    }
}
