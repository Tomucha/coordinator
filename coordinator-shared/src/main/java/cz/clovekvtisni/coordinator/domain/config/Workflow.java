package cz.clovekvtisni.coordinator.domain.config;

import org.simpleframework.xml.*;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Validate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private transient List<WorkflowState> workflowStatesList;

    private WorkflowState[] states;

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

    public WorkflowState[] getStates() {
        return states;
    }

    public Map<String, WorkflowState> getStateMap() {
        if (states == null) return new HashMap<String, WorkflowState>();
        Map<String, WorkflowState> map = new HashMap<String, WorkflowState>(states.length);
        for (WorkflowState state : states) {
            map.put(state.getId(), state);
        }

        return map;
    }

    @Commit
    public void ensureValidity() {
        if (workflowStatesList != null) {
            for (WorkflowState state : workflowStatesList) {
                state.setWorkflowId(id);
            }
            states = workflowStatesList.toArray(new WorkflowState[0]);
            workflowStatesList = null;
        }
    }

    @Validate
    public void validate() {
        if (states != null) {
            Map<String, WorkflowState> stateMap = getStateMap();
            for (WorkflowState state : states) {
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

    public WorkflowState getStartState() {
        if (states == null || states[0] == null)
            return null;

        return states[0];
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", canBeStartedBy=" + (canBeStartedBy == null ? null : Arrays.asList(canBeStartedBy)) +
                ", states=" + states +
                '}';
    }
}
