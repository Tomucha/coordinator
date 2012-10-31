package cz.clovekvtisni.coordinator.domain;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 31.10.12
 */
public class Organization extends AbstractStaticEntity {

    private String name;

    private String colorPrimary;

    private String colorSecondary;

    private boolean allowsRegistration;

    private boolean allowsPreRegistration;

    private List<String> preRegistrationEquipment;

    private List<String> preRegistrationSkills;

    public boolean isAllowsRegistration() {
        return allowsRegistration;
    }

    public void setAllowsRegistration(boolean allowsRegistration) {
        this.allowsRegistration = allowsRegistration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public void setColorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public String getColorSecondary() {
        return colorSecondary;
    }

    public void setColorSecondary(String colorSecondary) {
        this.colorSecondary = colorSecondary;
    }

    public boolean isAllowsPreRegistration() {
        return allowsPreRegistration;
    }

    public void setAllowsPreRegistration(boolean allowsPreRegistration) {
        this.allowsPreRegistration = allowsPreRegistration;
    }

    public List<String> getPreRegistrationEquipment() {
        return preRegistrationEquipment;
    }

    public void setPreRegistrationEquipment(List<String> preRegistrationEquipment) {
        this.preRegistrationEquipment = preRegistrationEquipment;
    }

    public List<String> getPreRegistrationSkills() {
        return preRegistrationSkills;
    }

    public void setPreRegistrationSkills(List<String> preRegistrationSkills) {
        this.preRegistrationSkills = preRegistrationSkills;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "name='" + name + '\'' +
                '}';
    }
}
