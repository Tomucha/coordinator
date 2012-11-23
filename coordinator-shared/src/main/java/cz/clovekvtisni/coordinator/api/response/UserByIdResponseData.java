package cz.clovekvtisni.coordinator.api.response;

import cz.clovekvtisni.coordinator.domain.User;

import java.util.List;

public class UserByIdResponseData implements ApiResponseData {

    private User[] users;

    public UserByIdResponseData() {
    }

    public UserByIdResponseData(List<User> users) {
        setUsers(users);
    }

    public UserByIdResponseData(User... users) {
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

    public User getFirst() {
        if (users == null || users.length == 0)
            return null;
        else
            return users[0];
    }
}
