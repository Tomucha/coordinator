package cz.clovekvtisni.coordinator.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root(name = "transition")
public class WorkflowTransition extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Attribute
    private String name;

    @Text(required = false)
    private String description;

    @Attribute(name = "from_state_id", required = false)
    private String fromStateId;

    @Attribute(name = "to_state_id", required = false)
    private String toStateId;

    @Attribute(name = "allowed_for_role", required = false, empty = "")
    private String[] allowedForRole;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFromStateId() {
        return fromStateId;
    }

    public String getToStateId() {
        return toStateId;
    }

    public String[] getAllowedForRole() {
        return allowedForRole;
    }

    @Override
    public String toString() {
        return "WorkflowTransition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", fromStateId='" + fromStateId + '\'' +
                ", toStateId='" + toStateId + '\'' +
                ", allowedForRole=" + Arrays.asList(allowedForRole) +
                '}';
    }
}
