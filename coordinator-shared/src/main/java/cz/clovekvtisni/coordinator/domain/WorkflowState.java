package cz.clovekvtisni.coordinator.domain;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class WorkflowState extends AbstractStaticEntity {

    private String workflowId;

    private String name;

    private String description;

    private boolean requiresAssignment;

    private List<String> visibleForRole;

    private List<String> editableForRole;

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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

    public boolean isRequiresAssignment() {
        return requiresAssignment;
    }

    public void setRequiresAssignment(boolean requiresAssignment) {
        this.requiresAssignment = requiresAssignment;
    }

    public List<String> getVisibleForRole() {
        return visibleForRole;
    }

    public void setVisibleForRole(List<String> visibleForRole) {
        this.visibleForRole = visibleForRole;
    }

    public List<String> getEditableForRole() {
        return editableForRole;
    }

    public void setEditableForRole(List<String> editableForRole) {
        this.editableForRole = editableForRole;
    }

    @Override
    public String toString() {
        return "WorkflowState{" +
                "workflowId='" + workflowId + '\'' +
                ", name='" + name + '\'' +
                ", requiresAssignment=" + requiresAssignment +
                '}';
    }
}
