package cz.clovekvtisni.coordinator.server.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PermissionCheckResultModel implements Serializable {

    private static final long serialVersionUID = 8334301416264730811L;

    private Map<String, Boolean> resultMap = new HashMap<String, Boolean>();

    public PermissionCheckResultModel addResult(String name, boolean result) {
        if (resultMap.containsKey(name)) {
            throw new IllegalStateException("model already contains result named " + name);
        }
        resultMap.put(name, result);
        return this;
    }

    public boolean isPermitted(String name, String... otherNames) {
        Boolean result = resultMap.get(name);
        if (result == null) {
            throw new IllegalStateException("no found result named " + name);
        }
        if (!result) {
            return false;
        }
        for (String otherName : otherNames) {
            result = resultMap.get(otherName);
            if (result == null) {
                throw new IllegalStateException("no found result named " + name);
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllPermitted() {
        for (Boolean permitted : resultMap.values()) {
            if (!Boolean.TRUE.equals(permitted)) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnyPermitted() {
        for (Boolean permitted : resultMap.values()) {
            if (Boolean.TRUE.equals(permitted)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PermissionCheckResultModel{");
        boolean addSeparator = false;
        for (Map.Entry<String, Boolean> entry : resultMap.entrySet()) {
            if (addSeparator) sb.append(", ");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            addSeparator = true;
        }
        return sb.append("}").toString();
    }
}
