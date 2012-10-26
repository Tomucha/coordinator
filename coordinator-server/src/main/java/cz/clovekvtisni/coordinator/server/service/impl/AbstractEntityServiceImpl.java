package cz.clovekvtisni.coordinator.server.service.impl;

import com.googlecode.objectify.Objectify;
import cz.clovekvtisni.coordinator.server.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: tomas
 * Date: 10/26/12
 * Time: 11:54 PM
 */
public class AbstractEntityServiceImpl extends AbstractServiceImpl {

    @Autowired
    protected SystemService systemService;

}
