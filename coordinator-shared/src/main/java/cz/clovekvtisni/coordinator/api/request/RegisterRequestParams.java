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

    public RegisterRequestParams() {
    }

    public RegisterRequestParams(User newUser, List<UserSkill> skills) {
        this.newUser = newUser;
    }

    public User getNewUser() {
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    @Override
    public String getSignature() {
        return null;  // TODO
    }
}
