package net.myspring.basic.modules.hr.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import net.myspring.basic.modules.hr.service.DutyOvertimeService;

@Controller
@RequestMapping(value = "hr/dutyOvertime")
public class DutyOvertimeController {

    @Autowired
    private DutyOvertimeService dutyOvertimeService;

}
