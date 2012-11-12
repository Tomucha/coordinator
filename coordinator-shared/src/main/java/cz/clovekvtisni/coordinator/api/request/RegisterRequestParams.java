package cz.clovekvtisni.coordinator.api.request;

import cz.clovekvtisni.coordinator.domain.User;
import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.domain.UserSkill;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class RegisterRequestParams implements RequestParams {

    private User newUser;

    private List<UserSkill> skills;

    private List<UserEquipment> equipments;

    public RegisterRequestParams() {
    }

    public RegisterRequestParams(User newUser, List<UserSkill> skills, List<UserEquipment> equipments) {
        this.newUser = newUser;
        this.skills = skills;
        this.equipments = equipments;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public List<UserSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<UserSkill> skills) {
        this.skills = skills;
    }

    public List<UserEquipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<UserEquipment> equipments) {
        this.equipments = equipments;
    }

    @Override
    public String getSignature() {
        return null;  // TODO
    }
}
