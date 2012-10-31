package cz.clovekvtisni.coordinator.domain;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class Equipment extends AbstractStaticEntity {

    private String name;

    private String description;

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

    @Override
    public String toString() {
        return "Equipment{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
