package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import cz.clovekvtisni.coordinator.domain.OrganizationInEvent;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.exception.MaException;
import cz.clovekvtisni.coordinator.exception.NotFoundException;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.domain.OrganizationInEventEntity;
import cz.clovekvtisni.coordinator.server.filter.OrganizationFilter;
import cz.clovekvtisni.coordinator.server.service.OrganizationService;
import cz.clovekvtisni.coordinator.server.tool.objectify.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Service("organizationService")
public class OrganizationServiceImpl extends AbstractEntityServiceImpl implements OrganizationService {

    @Autowired
    private CoordinatorConfig config;

    @Override
    public Organization findById(String id, long flags) {
        if (id == null) return null;

        for (Organization organization : config.getOrganizationList()) {
            if (id.equals(organization.getId())) {
                return organization;
            }
        }

        return null;
    }

    @Override
    public ResultList<Organization> findByFilter(OrganizationFilter filter) {
        List<Organization> organizationList = config.getOrganizationList();

        return new ResultList<Organization>(organizationList, null);
    }
}
