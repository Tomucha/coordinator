package cz.clovekvtisni.coordinator.api.request;

import cz.clovekvtisni.coordinator.util.SignatureTool;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/6/11
 * Time: 11:24 AM
 */
public class ApiRequest {
    
    private String signature;
    private String token;
    private Object data;
    private String sessionId;
    
    private static long counter = 0;
    
    private static final Object LOCK = new Object();

    public ApiRequest() {
    }
    
    public ApiRequest(String sessionId, String deviceId, Object data, String secret) {
        long c;
        synchronized (LOCK) {
            if (++counter == Long.MAX_VALUE) counter = 1;
            c = counter;
        }
        this.token = deviceId + ";" + System.currentTimeMillis() + ";" + c;
        this.sessionId = sessionId;
        this.data = data;
        this.signature = SignatureTool.signApi(SignatureTool.computeHash(data), secret);
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "ApiRequest{" +
                "signature='" + signature + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
