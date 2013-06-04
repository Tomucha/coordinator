package cz.clovekvtisni.coordinator.domain.config;

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

    @Attribute(required = false)
    private RolePermission[] permissions;

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

    public RolePermission[] getPermissions() {
        return permissions;
    }

    public void setPermissions(RolePermission[] permissions) {
        this.permissions = permissions;
    }

    public boolean hasAnyPermission(RolePermission... needed) {
        if (permissions == null)
            return false;

        List<RolePermission> havePermissions = Arrays.asList(permissions);
        for (RolePermission permission : needed) {
            if (havePermissions.contains(permission))
                return true;
        }
        return false;
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
