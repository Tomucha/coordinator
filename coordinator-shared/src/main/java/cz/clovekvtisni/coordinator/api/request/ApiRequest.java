package cz.clovekvtisni.coordinator.api.request;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/6/11
 * Time: 11:24 AM
 */
public class ApiRequest {

    private Object data;
    private String authKey;
    
    private static long counter = 0;
    
    private static final Object LOCK = new Object();

    public ApiRequest() {
    }
    
    public ApiRequest(String authKey, String deviceId, Object data, String secret) {
        long c;
        synchronized (LOCK) {
            if (++counter == Long.MAX_VALUE) counter = 1;
            c = counter;
        }
        this.authKey = authKey;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                ", data=" + data +
                ", authKey='" + authKey + '\'' +
                '}';
    }
}
