package cz.clovekvtisni.coordinator.server.web.model;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 9.12.12
 */
public class UserMultiSelection {

    private Long eventId;

    private Long[] selectedUsers;

    private String selectedAction;

    private Long selectedTaskId;

    public Long[] getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(Long[] selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public String getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(String selectedAction) {
        this.selectedAction = selectedAction;
    }

    public Long getSelectedTaskId() {
        return selectedTaskId;
    }

    public void setSelectedTaskId(Long selectedTaskId) {
        this.selectedTaskId = selectedTaskId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
