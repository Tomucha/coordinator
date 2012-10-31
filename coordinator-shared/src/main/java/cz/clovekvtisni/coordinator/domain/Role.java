package cz.clovekvtisni.coordinator.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
@Root
public class Role extends AbstractStaticEntity {

    @Attribute
    protected String id;

    @Attribute
    private String name;

    @Text
    private String description;

    @Attribute(name = "extends_role_id")
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
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", extendsRoleId='" + extendsRoleId + '\'' +
                '}';
    }
}
