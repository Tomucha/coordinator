package cz.clovekvtisni.coordinator.server.web.controller.api.v1;

import cz.clovekvtisni.coordinator.api.response.ApiResponse;
import cz.clovekvtisni.coordinator.api.response.OrganizationListResponseData;
import cz.clovekvtisni.coordinator.domain.config.Organization;
import cz.clovekvtisni.coordinator.server.service.OrganizationService;
import cz.clovekvtisni.coordinator.server.service.ResultList;
import cz.clovekvtisni.coordinator.server.web.controller.api.AbstractApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: jka
 * Date: 5.11.12
 */
@Controller
@RequestMapping("/api/v1/organization")
public class OrganizationApiController extends AbstractApiController {

    @Autowired
    private OrganizationService organizationService;

    @RequestMapping("/list")
    public @ResponseBody ApiResponse list(HttpServletRequest request) {
        ResultList<Organization> list = organizationService.findByFilter(null);

        return okResult(new OrganizationListResponseData(list.getResult()));
    }
}

