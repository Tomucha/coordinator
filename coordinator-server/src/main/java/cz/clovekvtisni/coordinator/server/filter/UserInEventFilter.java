package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.UserInEventEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 22.11.12
 */
public class UserInEventFilter extends NoDeletedFilter<UserInEventEntity> {

    private Long userIdVal;

    private Operator userIdOp = Operator.EQ;

    private Long eventIdVal;

    private Operator eventIdOp = Operator.EQ;

    private Date modifiedDateVal;

    private Operator modifiedDateOp = Operator.EQ;

    private List<String> geoCellsVal;

    private Operator geoCellsOp = Operator.IN;

    public Long getUserIdVal() {
        return userIdVal;
    }

    public void setUserIdVal(Long userIdVal) {
        this.userIdVal = userIdVal;
    }

    public Operator getUserIdOp() {
        return userIdOp;
    }

    public void setUserIdOp(Operator userIdOp) {
        this.userIdOp = userIdOp;
    }

    public Long getEventIdVal() {
        return eventIdVal;
    }

    public void setEventIdVal(Long eventIdVal) {
        this.eventIdVal = eventIdVal;
    }

    public Operator getEventIdOp() {
        return eventIdOp;
    }

    public void setEventIdOp(Operator eventIdOp) {
        this.eventIdOp = eventIdOp;
    }

    public Date getModifiedDateVal() {
        return modifiedDateVal;
    }

    public void setModifiedDateVal(Date modifiedDateVal) {
        this.modifiedDateVal = modifiedDateVal;
    }

    public Operator getModifiedDateOp() {
        return modifiedDateOp;
    }

    public void setModifiedDateOp(Operator modifiedDateOp) {
        this.modifiedDateOp = modifiedDateOp;
    }

    public List<String> getGeoCellsVal() {
        return geoCellsVal;
    }

    public void setGeoCellsVal(List<String> geoCellsVal) {
        this.geoCellsVal = geoCellsVal;
    }

    public Operator getGeoCellsOp() {
        return geoCellsOp;
    }

    public void setGeoCellsOp(Operator geoCellsOp) {
        this.geoCellsOp = geoCellsOp;
    }

    @Override
    public Class<UserInEventEntity> getEntityClass() {
        return UserInEventEntity.class;
    }
}
