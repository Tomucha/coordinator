package cz.clovekvtisni.coordinator.api.request;

public class EmptyRequestParams implements RequestParams {
    @Override
    public String getSignature() {
        return "";
    }
}
