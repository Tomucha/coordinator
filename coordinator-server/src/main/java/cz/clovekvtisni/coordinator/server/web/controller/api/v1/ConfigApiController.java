package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.request.EmptyRequestParams;
import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.ConfigResponse;
import cz.clovekvtisni.coordinator.domain.config.*;
import cz.clovekvtisni.coordinator.server.domain.CoordinatorConfig;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 7.11.12
 */
@Controller
@RequestMapping("/api/v1/config")
public class ConfigApiController extends AbstractApiController {

    @Autowired
    private CoordinatorConfig config;

    @RequestMapping
    public @ResponseBody ApiResponse global(HttpServletRequest request) {
        parseRequestAnonymous(request, EmptyRequestParams.class);
        ConfigResponse response = new ConfigResponse();
        response.setEquipmentList(config.getEquipmentList().toArray(new Equipment[0]));
        response.setOrganizationList(config.getOrganizationList().toArray(new Organization[0]));
        response.setPoiCategoryList(config.getPoiCategoryList().toArray(new PoiCategory[0]));
        response.setRoleList(config.getRoleList().toArray(new Role[0]));
        response.setSkillList(config.getSkillList().toArray(new Skill[0]));
        response.setWorkflowList(config.getWorkflowList().toArray(new Workflow[0]));

        return okResult(response);
    }
}
