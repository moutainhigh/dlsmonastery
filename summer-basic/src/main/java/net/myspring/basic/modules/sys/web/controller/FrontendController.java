package net.myspring.basic.modules.sys.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import net.myspring.basic.modules.sys.service.FrontendService;

@Controller
@RequestMapping(value = "sys/frontend")
public class FrontendController {

    @Autowired
    private FrontendService frontendService;

}
