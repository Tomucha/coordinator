package cz.clovekvtisni.coordinator.domain;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class PoiCategory extends AbstractStaticEntity {

    private String name;

    private String description;

    private String icon;

    private String workflowId;

    private boolean important;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    @Override
    public String toString() {
        return "PoiCategory{" +
                "name='" + name + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", important=" + important +
                '}';
    }
}
