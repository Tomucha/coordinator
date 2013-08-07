package cz.clovekvtisni.coordinator.server.web.model;

import cz.clovekvtisni.coordinator.server.domain.UserEntity;
import cz.clovekvtisni.coordinator.server.filter.UserFilter;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;
import cz.clovekvtisni.coordinator.server.validation.ValidatorTool;
import cz.clovekvtisni.coordinator.util.ValueTool;

public class UserFilterParams {

    private String name;

    private String organizationId;

    private String email;

    public UserFilterParams() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserFilter populateUserFilter(UserFilter filter) {
        if (!ValueTool.isEmpty(getOrganizationId())) {
            filter.setOrganizationIdOp(Filter.Operator.EQ);
            filter.setOrganizationIdVal(getOrganizationId());
        }

        filter.addAfterLoadCallback(new Filter.AfterLoadCallback<UserEntity>() {
            @Override
            public boolean accept(UserEntity user) {
                if (user == null)
                    return false;
                if (
                        getName() != null &&
                        (user.getFullName() == null || !user.getFullName().toLowerCase().contains(getName().toLowerCase()))
                    )
                    return false;
                if (
                        getEmail() != null &&
                                (user.getEmail() == null || !user.getEmail().toLowerCase().contains(getEmail().toLowerCase()))
                        )
                    return false;

                return true;
            }
        });
        return filter;
    }
}
