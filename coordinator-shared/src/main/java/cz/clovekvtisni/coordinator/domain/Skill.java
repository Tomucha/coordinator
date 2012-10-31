package cz.clovekvtisni.coordinator.domain;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class Skill extends AbstractStaticEntity {

    private String name;

    private String description;

    private boolean mustVerify;

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

    public boolean isMustVerify() {
        return mustVerify;
    }

    public void setMustVerify(boolean mustVerify) {
        this.mustVerify = mustVerify;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", mustVerify=" + mustVerify +
                '}';
    }
}
