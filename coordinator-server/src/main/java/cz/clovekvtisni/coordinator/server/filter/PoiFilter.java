package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.Poi;
import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class PoiFilter extends NoDeletedFilter<PoiEntity> {

    private Long eventIdVal;

    private Operator eventIdOp = Operator.EQ;

    private String organizationIdVal;

    private Operator organizationIdOp = Operator.EQ;

    private String workflowIdVal;

    private Operator workflowIdOp = Operator.EQ;

    private String workflowStateIdVal;

    private Operator workflowStateIdOp = Operator.EQ;

    private Date modifiedDateVal;

    private Operator modifiedDateOp = Operator.EQ;

    private List<String> geoCellsVal;

    private Operator geoCellsOp = Operator.IN;
    
    private String[] visibleForRolesVal;
    
    private Operator visibleForRolesOp = Operator.IN;

    public Long getEventIdVal() {
        return eventIdVal;
    }

    public void setEventIdVal(Long eventIdVal) {
        this.eventIdVal = eventIdVal;
    }

    public Operator getEventIdOp() {
        return eventIdOp;
    }

    public void setEventIdOp(Operator eventIdOp) {
        this.eventIdOp = eventIdOp;
    }

    public String getOrganizationIdVal() {
        return organizationIdVal;
    }

    public void setOrganizationIdVal(String organizationIdVal) {
        this.organizationIdVal = organizationIdVal;
    }

    public Operator getOrganizationIdOp() {
        return organizationIdOp;
    }

    public void setOrganizationIdOp(Operator organizationIdOp) {
        this.organizationIdOp = organizationIdOp;
    }

    public String getWorkflowIdVal() {
        return workflowIdVal;
    }

    public void setWorkflowIdVal(String workflowIdVal) {
        this.workflowIdVal = workflowIdVal;
    }

    public Operator getWorkflowIdOp() {
        return workflowIdOp;
    }

    public void setWorkflowIdOp(Operator workflowIdOp) {
        this.workflowIdOp = workflowIdOp;
    }

    public Date getModifiedDateVal() {
        return modifiedDateVal;
    }

    public void setModifiedDateVal(Date modifiedDateVal) {
        this.modifiedDateVal = modifiedDateVal;
    }

    public Operator getModifiedDateOp() {
        return modifiedDateOp;
    }

    public void setModifiedDateOp(Operator modifiedDateOp) {
        this.modifiedDateOp = modifiedDateOp;
    }

    public String getWorkflowStateIdVal() {
        return workflowStateIdVal;
    }

    public void setWorkflowStateIdVal(String workflowStateIdVal) {
        this.workflowStateIdVal = workflowStateIdVal;
    }

    public Operator getWorkflowStateIdOp() {
        return workflowStateIdOp;
    }

    public void setWorkflowStateIdOp(Operator workflowStateIdOp) {
        this.workflowStateIdOp = workflowStateIdOp;
    }

    public List<String> getGeoCellsVal() {
        return geoCellsVal;
    }

    public void setGeoCellsVal(List<String> geoCellsVal) {
        this.geoCellsVal = geoCellsVal;
    }

    public Operator getGeoCellsOp() {
        return geoCellsOp;
    }

    public void setGeoCellsOp(Operator geoCellsOp) {
        this.geoCellsOp = geoCellsOp;
    }

    public Operator getVisibleForRolesOp() {
        return visibleForRolesOp;
    }

    public void setVisibleForRolesOp(Operator visibleForRolesOp) {
        this.visibleForRolesOp = visibleForRolesOp;
    }

    public String[] getVisibleForRolesVal() {
        return visibleForRolesVal;
    }

    public void setVisibleForRolesVal(String[] visibleForRolesVal) {
        this.visibleForRolesVal = visibleForRolesVal;
    }

    @Override
    public Class<PoiEntity> getEntityClass() {
        return PoiEntity.class;
    }
}
