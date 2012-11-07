package cz.clovekvtisni.coordinator.domain.config;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Commit;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root(name = "state")
public class WorkflowState extends AbstractStaticEntity {

    @Attribute
    private String id;

    private String workflowId;

    @Attribute
    private String name;

    @Element(required = false)
    private String description;

    @Attribute(name = "requires_assignment", required = false)
    private boolean requiresAssignment;

    @Attribute(name = "visible_for_role", required = false, empty = "")
    private String[] visibleForRole;

    @Attribute(name = "editable_for_role", required = false, empty = "")
    private String[] editableForRole;

    @ElementList(type = WorkflowTransition.class, name = "transition", inline = true, required = false)
    private transient List<WorkflowTransition> transitionsList;

    private WorkflowTransition[] transitions;

    public String getId() {
        return id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequiresAssignment() {
        return requiresAssignment;
    }

    public String[] getVisibleForRole() {
        return visibleForRole;
    }

    public String[] getEditableForRole() {
        return editableForRole;
    }

    public WorkflowTransition[] getTransitions() {
        return transitions;
    }

    @Commit
    public void ensureValidity() {
        if (transitionsList != null) {
            transitions = transitionsList.toArray(new WorkflowTransition[0]);
            transitionsList = null;
        }

    }
    /**
     * Called by parent domain object thru deserialization.
     * @param workflowId parent workflow id
     */
    void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public String toString() {
        return "WorkflowState{" +
                "id='" + id + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", name='" + name + '\'' +
                ", requiresAssignment=" + requiresAssignment +
                ", visibleForRole=" + Arrays.asList(visibleForRole) +
                ", editableForRole=" + Arrays.asList(editableForRole) +
                '}';
    }
}
