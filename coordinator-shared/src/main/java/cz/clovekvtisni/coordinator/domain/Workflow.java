package cz.clovekvtisni.coordinator.domain;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Validate;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root
public class Workflow extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Attribute
    private String name;

    @Element(required = false)
    private String description;

    @Attribute(name = "can_be_started_by", required = false, empty = "")
    private String[] canBeStartedBy;

    @ElementList(type = WorkflowState.class, name = "state", required = false, inline = true)
    private List<WorkflowState> workflowStates;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getCanBeStartedBy() {
        return canBeStartedBy;
    }

    public String getId() {
        return id;
    }

    public List<WorkflowState> getStates() {
        return workflowStates;
    }

    public Map<String, WorkflowState> getStateMap() {
        if (workflowStates == null) return new HashMap<String, WorkflowState>();
        Map<String, WorkflowState> map = new HashMap<String, WorkflowState>(workflowStates.size());
        for (WorkflowState state : workflowStates) {
            map.put(state.getId(), state);
        }

        return map;
    }

    @Commit
    public void ensureChildrenParent() {
        if (workflowStates != null) {
            for (WorkflowState state : workflowStates) {
                state.setWorkflowId(id);
            }
        }
    }

    @Validate
    public void validate() {
        if (workflowStates != null) {
            Map<String, WorkflowState> stateMap = getStateMap();
            for (WorkflowState state : workflowStates) {
                if (state.getTransitions() != null) {
                    for (WorkflowTransition transition : state.getTransitions()) {
                        if (
                                (transition.getFromStateId() != null && !stateMap.containsKey(transition.getFromStateId()))
                                        || (transition.getToStateId() != null && !stateMap.containsKey(transition.getToStateId()))
                                ) {
                            throw new IllegalStateException("Inconsistent configuration of workflow transitions. Transition '" + transition.getId() + "' refers to nonexistent state");
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", canBeStartedBy=" + (canBeStartedBy == null ? null : Arrays.asList(canBeStartedBy)) +
                ", workflowStates=" + workflowStates +
                '}';
    }
}
