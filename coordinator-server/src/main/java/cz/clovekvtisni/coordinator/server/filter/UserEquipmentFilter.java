package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.UserEquipment;
import cz.clovekvtisni.coordinator.server.domain.UserEquipmentEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 14.11.12
 */
public class UserEquipmentFilter extends NoDeletedFilter<UserEquipmentEntity> {

    private Long userIdVal;

    private Operator userIdOp = Operator.EQ;

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

    @Override
    public Class<UserEquipmentEntity> getEntityClass() {
        return UserEquipmentEntity.class;
    }
}
