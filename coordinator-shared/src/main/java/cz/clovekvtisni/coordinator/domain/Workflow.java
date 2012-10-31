package cz.clovekvtisni.coordinator.domain;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class Workflow extends AbstractStaticEntity {

    private String name;

    private String description;

    private List<String> canBeStartedBy;

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

    public List<String> getCanBeStartedBy() {
        return canBeStartedBy;
    }

    public void setCanBeStartedBy(List<String> canBeStartedBy) {
        this.canBeStartedBy = canBeStartedBy;
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "name='" + name + '\'' +
                '}';
    }
}
