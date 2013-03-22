package cz.clovekvtisni.coordinator.domain.config;

import cz.clovekvtisni.coordinator.domain.config.AbstractStaticEntity;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class Organization extends AbstractStaticEntity {

    @Attribute
    private String id;

    @Attribute
    private String name;

    @Text(required = false)
    private String description;

    @Attribute(name = "color_primary")
    private String colorPrimary;

    @Attribute(name = "color_secondary")
    private String colorSecondary;

    @Attribute(name = "allows_registration", required = false)
    private boolean allowsRegistration;

    @Attribute(name = "allows_pre_registration", required = false)
    private boolean allowsPreRegistration;

    @Attribute(name = "pre_registration_equipment", required = false, empty = "")
    private String[] preRegistrationEquipment;

    @Attribute(name = "pre_registration_skill", required = false, empty = "")
    private String[] preRegistrationSkills;

    @Attribute
    private String icon;

    public String getId() {
        return id;
    }

    public boolean isAllowsRegistration() {
        return allowsRegistration;
    }

    public void setAllowsRegistration(boolean allowsRegistration) {
        this.allowsRegistration = allowsRegistration;
    }

    public String getName() {
        return name;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public String getColorSecondary() {
        return colorSecondary;
    }

    public boolean isAllowsPreRegistration() {
        return allowsPreRegistration;
    }

    public String[] getPreRegistrationEquipment() {
        return preRegistrationEquipment;
    }

    public String[] getPreRegistrationSkills() {
        return preRegistrationSkills;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", colorPrimary='" + colorPrimary + '\'' +
                ", colorSecondary='" + colorSecondary + '\'' +
                ", allowsRegistration=" + allowsRegistration +
                ", allowsPreRegistration=" + allowsPreRegistration +
                ", preRegistrationEquipment=" + (preRegistrationEquipment == null ? null : Arrays.asList(preRegistrationEquipment)) +
                ", preRegistrationSkills=" + (preRegistrationSkills == null ? null : Arrays.asList(preRegistrationSkills)) +
                '}';
    }
}
