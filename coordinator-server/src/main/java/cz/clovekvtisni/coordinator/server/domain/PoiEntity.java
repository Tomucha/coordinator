package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cached;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Indexed;
import com.googlecode.objectify.annotation.Unindexed;
import cz.clovekvtisni.coordinator.domain.Poi;

import javax.persistence.Id;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Unindexed
@Cached
@Entity(name = "Poi")
public class PoiEntity extends AbstractPersistentEntity<Poi, PoiEntity> {

    @Id
    private Long id;

    private Long poiCategoryId;

    @Indexed
    private Long workflowId;

    private Long workflowStateId;

    @Indexed
    private Long[] userId;

    private Double latitude;

    private Double longitude;

    private Long precission;

    private boolean confirmed;

    public PoiEntity() {
    }

    @Override
    protected Poi createTargetEntity() {
        return new Poi();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public Key<PoiEntity> getKey() {
        return Key.create(PoiEntity.class, id);
    }

    @Override
    public String toString() {
        return "PoiEntity{" +
                "id=" + id +
                ", poiCategoryId=" + poiCategoryId +
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
