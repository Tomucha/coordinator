package cz.clovekvtisni.coordinator.api.request;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 23.11.12
 */
public class UserByIdRequestParams implements RequestParams {

    private Long[] byId;

    public UserByIdRequestParams() {
    }

    public Long[] getById() {
        return byId;
    }

    public void setById(Long... byId) {
        this.byId = byId;
    }

    @Override
    public String getSignature() {
        StringBuilder builder = new StringBuilder("");
        if (byId != null) {
            for (Long s : byId) {
                builder.append(builder.length() > 0 ? "~" + s : "" + s);
            }
        }
        return builder.toString();
    }
}
