package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.server.domain.PoiEntity;
import cz.clovekvtisni.coordinator.server.domain.UserGroupEntity;
import cz.clovekvtisni.coordinator.server.tool.objectify.NoDeletedFilter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 6.11.12
 */
public class UserGroupFilter extends NoDeletedFilter<UserGroupEntity> {

    private Long eventIdVal;

    private Operator eventIdOp = Operator.EQ;

    private String organizationIdVal;

    private Operator organizationIdOp = Operator.EQ;

    private Operator Op = Operator.EQ;

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

    public String getOrganizationIdVal() {
        return organizationIdVal;
    }

    public void setOrganizationIdVal(String organizationIdVal) {
        this.organizationIdVal = organizationIdVal;
    }

    public Operator getOrganizationIdOp() {
        return organizationIdOp;
    }

    public void setOrganizationIdOp(Operator organizationIdOp) {
        this.organizationIdOp = organizationIdOp;
    }

    public Operator getOp() {
        return Op;
    }

    public void setOp(Operator op) {
        Op = op;
    }

    @Override
    public Class<UserGroupEntity> getEntityClass() {
        return UserGroupEntity.class;
    }
}
