package cz.clovekvtisni.coordinator.server.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.impl.ref.StdRef;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 21.11.12
 */
@Cache
@Entity
public class UserAuthKey {

    @Id
    private String authKey;

    @Load
    Ref<UserEntity> refToUser;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public UserEntity getUser() {
        return refToUser.getValue();
    }

    public void setUser(UserEntity user) {
        this.refToUser = new StdRef<UserEntity>(user.getKey(), user);
    }
}
