package cz.clovekvtisni.coordinator.domain;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class Role extends AbstractStaticEntity {

    private String name;

    private String description;

    private String extendsRoleId;

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

    public String getExtendsRoleId() {
        return extendsRoleId;
    }

    public void setExtendsRoleId(String extendsRoleId) {
        this.extendsRoleId = extendsRoleId;
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", extendsRoleId='" + extendsRoleId + '\'' +
                '}';
    }
}
