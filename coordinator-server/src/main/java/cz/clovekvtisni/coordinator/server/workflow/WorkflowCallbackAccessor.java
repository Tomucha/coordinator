package cz.clovekvtisni.coordinator.server.workflow;

import cz.clovekvtisni.coordinator.server.workflow.callback.WorkflowCallback;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkflowCallbackAccessor {

    private Map<String, WorkflowCallback> callbackMap = new HashMap<String, WorkflowCallback>();

    public void register(WorkflowCallback callback) {
        String key = callback.getClass().getSimpleName();
        key = key.replaceAll("^(\\w+)Callback$", "$1");
        callbackMap.put(key.toLowerCase(), callback);
    }

    public WorkflowCallback getCallbackByKey(String key) {
        return callbackMap.get(key.toLowerCase());
    }
}
