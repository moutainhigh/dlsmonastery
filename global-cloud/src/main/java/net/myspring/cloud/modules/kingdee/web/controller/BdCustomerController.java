package net.myspring.cloud.modules.kingdee.web.controller;

import net.myspring.cloud.modules.kingdee.domain.BdCustomer;
import net.myspring.cloud.modules.kingdee.service.BdCustomerService;
import net.myspring.cloud.modules.kingdee.web.query.BdCustomerQuery;
import net.myspring.common.dto.NameValueDto;
import net.myspring.util.time.LocalDateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by lihx on 2017/5/12.
 */
@RestController
@RequestMapping(value = "kingdee/bdCustomer")
public class BdCustomerController {
    @Autowired
    private BdCustomerService bdCustomerService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<BdCustomer> list(Pageable pageable, BdCustomerQuery bdCustomerQuery){
        Page<BdCustomer> page = bdCustomerService.findPageIncludeForbid(pageable,bdCustomerQuery);
        return page;
    }


    @RequestMapping(value = "findByNameLike")
    public List<BdCustomer> findByNameLike(String name){
        List<BdCustomer> bdCustomerList = bdCustomerService.findByNameLike(name);
        return bdCustomerList;
    }

    @RequestMapping(value = "findAll")
    public List<BdCustomer>  findAll(){
        return bdCustomerService.findAll();
    }

    @RequestMapping(value = "findByMaxModifyDate")
    public List<BdCustomer> findByMaxModifyDate(String modifyDate){
        LocalDateTime maxModifyDate = LocalDateTimeUtils.parse(modifyDate,"yyyy-MM-dd HH:mm:ss");
        return bdCustomerService.findByMaxModifyDate(maxModifyDate);
    }
}
