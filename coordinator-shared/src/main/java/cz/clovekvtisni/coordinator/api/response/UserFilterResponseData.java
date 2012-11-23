package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.User;

import java.util.List;

public class UserFilterResponseData implements ApiResponseData {

    private User[] users;

    public UserFilterResponseData() {
    }

    public UserFilterResponseData(List<User> users) {
        setUsers(users);
    }

    public UserFilterResponseData(User... users) {
        this.users = users;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users.toArray(new User[0]);
    }

    public void setUsers(User[] users) {
        this.users = users;
    }
}
