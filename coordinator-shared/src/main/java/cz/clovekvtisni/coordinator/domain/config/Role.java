package cz.clovekvtisni.coordinator.domain.config;

import cz.clovekvtisni.coordinator.domain.config.AbstractStaticEntity;
import org.simpleframework.xml.Attribute;
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

    @Text(required = false)
    private String description;

    @Attribute(name = "extends_role_id", required = false)
    private String extendsRoleId;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getExtendsRoleId() {
        return extendsRoleId;
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
