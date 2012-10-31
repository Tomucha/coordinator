package cz.clovekvtisni.coordinator.domain;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class WorkflowTransition extends AbstractStaticEntity {

    private String name;

    private String description;

    private String fromStateId;

    private String toStateId;

    private List<String> allowedForRole;

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

    public String getFromStateId() {
        return fromStateId;
    }

    public void setFromStateId(String fromStateId) {
        this.fromStateId = fromStateId;
    }

    public String getToStateId() {
        return toStateId;
    }

    public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }

    public List<String> getAllowedForRole() {
        return allowedForRole;
    }

    public void setAllowedForRole(List<String> allowedForRole) {
        this.allowedForRole = allowedForRole;
    }

    @Override
    public String toString() {
        return "WorkflowTransition{" +
                "name='" + name + '\'' +
                ", fromStateId='" + fromStateId + '\'' +
                ", toStateId='" + toStateId + '\'' +
                ", allowedForRole=" + allowedForRole +
                '}';
    }
}
