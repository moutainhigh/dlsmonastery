package net.myspring.future.modules.crm.web.controller;


import com.google.common.collect.Lists;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.dto.ProductDto;
import net.myspring.future.modules.basic.service.PricesystemService;
import net.myspring.future.modules.crm.domain.PricesystemChange;

import net.myspring.future.modules.crm.dto.PricesystemChangeDto;
import net.myspring.future.modules.crm.service.PricesystemChangeService;
import net.myspring.future.modules.crm.web.form.PricesystemChangeForm;
import net.myspring.future.modules.crm.web.query.PricesystemChangeQuery;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "crm/pricesystemChange")
public class PricesystemChangeController {

    @Autowired
    private PricesystemChangeService pricesystemChangeService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<PricesystemChangeDto> list(Pageable pageable,PricesystemChangeQuery pricesystemChangeQuery) {
        Page<PricesystemChangeDto> page=pricesystemChangeService.findPage(pageable,pricesystemChangeQuery);
        return page;
    }

    @RequestMapping(value="getQuery")
    public PricesystemChangeQuery getQuery(PricesystemChangeQuery pricesystemChangeQuery){

        return pricesystemChangeService.getQuery(pricesystemChangeQuery);
    }

    @RequestMapping(value="getForm")
    public PricesystemChangeForm getForm(PricesystemChangeForm pricesystemChangeForm){
        return pricesystemChangeService.getForm(pricesystemChangeForm);
    }


    @RequestMapping(value = "save")
    public RestResponse save(PricesystemChangeForm pricesystemChangeForm) {
         pricesystemChangeService.save(pricesystemChangeForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "batchAudit")
    public RestResponse batchAudit(@RequestParam(value = "ids[]") String[] ids, Boolean pass){
        pricesystemChangeService.batchAudit(ids,pass);
        return new RestResponse("批量审核成功",ResponseCodeEnum.audited.name());
    }

    @RequestMapping(value="audit")
    public RestResponse audit(String id,Boolean pass){
        pricesystemChangeService.audit(id, pass);
        return new RestResponse("审核成功",ResponseCodeEnum.audited.name());
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id){
        if(StringUtils.isBlank(id)){
            throw new ServiceException("未选中一条记录");
        }
        pricesystemChangeService.delete(id);
        return new RestResponse("删除成功",ResponseCodeEnum.removed.name());
    }

}
