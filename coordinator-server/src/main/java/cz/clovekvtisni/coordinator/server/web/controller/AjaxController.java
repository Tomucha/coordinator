package cz.clovekvtisni.coordinator.server.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: tomucha
 * Date: 25.01.13
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
public class AjaxController {

    @RequestMapping
    public String show() {
        return "ajax/demo";
    }
}
