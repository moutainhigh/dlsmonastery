package net.myspring.future.modules.crm.web.controller;

import net.myspring.future.modules.crm.service.StoreAllotImeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "crm/storeAllotIme")
public class StoreAllotImeController {

    @Autowired
    private StoreAllotImeService storeAllotImeService;

}
