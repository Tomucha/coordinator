package cz.clovekvtisni.coordinator.server.domain;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.*;
import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.domain.config.PoiCategory;
import cz.clovekvtisni.coordinator.domain.config.Workflow;
import cz.clovekvtisni.coordinator.domain.config.WorkflowState;
import cz.clovekvtisni.coordinator.server.security.SecurityTool;
import cz.clovekvtisni.coordinator.util.CloneTool;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.*;

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

    private String subCategoryId;

    @Index
    private String workflowId;

    @Index
    private String workflowStateId;

    @Index
    private Set<Long> userIdList;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @Index
    private List<String> geoCells;

    private Long precission;

    private boolean confirmed;

    @Ignore
    @JsonIgnore
    private PoiCategory poiCategory;

    @Ignore
    @JsonIgnore
    private Workflow workflow;

    @Ignore
    @JsonIgnore
    private WorkflowState workflowState;

    @Index
    // FIXME: pocitat onSave
    private Set<String> visibleForRole;

    @Index
    private Boolean publicExport = false;

    public PoiEntity() {
    }

    @OnSave
    public void countGeoCells() {
        if (latitude != null && longitude != null) {

            // Transform it to a point
            Point p = new Point(latitude, longitude);

            // Generates the list of GeoCells
            List<String> cells = GeocellManager.generateGeoCell(p);
            geoCells = cells;

        } else {
            geoCells = Collections.EMPTY_LIST;
        }
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

    public Set<Long> getUserIdList() {
        if (userIdList == null) userIdList = new HashSet<Long>();
        return userIdList;
    }

    public void setUserIdList(Set<Long> userIdList) {
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
    @JsonIgnore
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
        return userIdList != null ? userIdList.size() : 0;
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

    public boolean isAssigned(UserEntity user) {
        if (userIdList == null || user == null)
            return false;
        return getUserIdList().contains(user.getId());
    }

    public Set<String> getVisibleForRole() {
        return visibleForRole;
    }

    public void setVisibleForRole(Set<String> visibleForRole) {
        this.visibleForRole = visibleForRole;
    }

    public boolean isImportant() {
        return poiCategory != null ? poiCategory.isImportant() : false;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Boolean getPublicExport() {
        return publicExport;
    }

    public void setPublicExport(Boolean publicExport) {
        if (deletedDate != null) {
            this.publicExport = false;
        } else {
            this.publicExport = publicExport;
        }
    }

    @OnSave
    public void updateDeletedState() {
        if (deletedDate != null) {
            setPublicExport(false);
            // TODO: a co role?
        }
    }

    public List<String> getGeoCells() {
        return geoCells;
    }

    public void setGeoCells(List<String> geoCells) {
        this.geoCells = geoCells;
    }

    @Override
    public String toString() {
        return "PoiEntity{" +
                "id=" + id +
                ", poiCategoryId=" + poiCategoryId + (subCategoryId == null ? "" : ("/" + subCategoryId)) +
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
