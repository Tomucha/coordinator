package cz.clovekvtisni.coordinator.server.filter;

import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.server.tool.objectify.Filter;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
public class OrganizationFilter extends Filter<Organization> {

    @Override
    public Class<Organization> getEntityClass() {
        return Organization.class;
    }
}
