package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.modules.input.service.HrEmpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lihx on 2017/4/6.
 */
@RestController
@RequestMapping(value = "input/hrEmpinfo")
public class HrEmpInfoController {
    @Autowired
    private HrEmpInfoService hrEmpInfoService;

    @RequestMapping(value = "getCode", method = RequestMethod.GET)
    public String getCode(String name) {
        return hrEmpInfoService.findFNumberByName(name);
    }
}
