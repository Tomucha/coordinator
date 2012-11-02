package cz.clovekvtisni.coordinator.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class PoiCategory extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Attribute
    private String name;

    @Text(required = false)
    private String description;

    @Attribute
    private String icon;

    @Attribute(name = "workflow_id", required = false)
    private String workflowId;

    @Attribute(required = false)
    private boolean important;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public boolean isImportant() {
        return important;
    }

    @Override
    public String toString() {
        return "PoiCategory{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", important=" + important +
                '}';
    }
}
