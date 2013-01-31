package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Cache
@Entity(name = "Poi")
public class PoiEntity extends AbstractPersistentEntity<Poi, PoiEntity> {

    @Id
    private Long id;

    @Index
    @NotNull
    private Long eventId;

    @Index
    @NotNull
    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @Index
    @NotNull
    private String organizationId;

    @NotNull
    private String poiCategoryId;

    @Index
    private String workflowId;

    private String workflowStateId;

    @Index
    private Long[] userIdList;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private Long precission;

    private boolean confirmed;

    @Ignore
    private PoiCategory poiCategory;

    @Ignore
    private Workflow workflow;

    @Ignore
    private WorkflowState workflowState;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getPoiCategoryId() {
        return poiCategoryId;
    }

    public void setPoiCategoryId(String poiCategoryId) {
        this.poiCategoryId = poiCategoryId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowStateId() {
        return workflowStateId;
    }

    public void setWorkflowStateId(String workflowStateId) {
        this.workflowStateId = workflowStateId;
    }

    public Long[] getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(Long[] userIdList) {
        this.userIdList = userIdList;
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

    public PoiCategory getPoiCategory() {
        return poiCategory;
    }

    public void setPoiCategory(PoiCategory poiCategory) {
        this.poiCategory = poiCategory;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
        if (workflow != null) {
            this.workflowId = workflow.getId();
        } else {
            this.workflowId = null;
        }
    }

    public WorkflowState getWorkflowState() {
        return workflowState;
    }

    public void setWorkflowState(WorkflowState workflowState) {
        this.workflowState = workflowState;
        if (workflowState != null) {
            this.workflowStateId = workflowState.getId();
        }
    }

    public int getUserCount() {
        return userIdList != null ? userIdList.length : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "PoiEntity{" +
                "id=" + id +
                ", poiCategoryId=" + poiCategoryId +
                ", workflowId=" + workflowId +
                ", workflowStateId=" + workflowStateId +
                ", userId=" + (userIdList == null ? null : Arrays.asList(userIdList)) +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", precission=" + precission +
                ", confirmed=" + confirmed +
                '}';
    }
}
