package net.myspring.future.modules.basic.web.controller;

import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.modules.basic.dto.ChainDto;
import net.myspring.future.modules.basic.service.ChainService;
import net.myspring.future.modules.basic.web.form.ChainForm;
import net.myspring.future.modules.basic.web.query.ChainQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "basic/chain")
public class ChainController {

    @Autowired
    private ChainService chainService;


    @RequestMapping(method = RequestMethod.GET)
    public Page<ChainDto> list(Pageable pageable, ChainQuery chainQuery){
        Page<ChainDto> page = chainService.findPage(pageable,chainQuery);
        return page;
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id) {
        chainService.logicDelete(id);
        return new RestResponse("删除成功", ResponseCodeEnum.removed.name());
    }

    @RequestMapping(value = "save")
    public RestResponse save(ChainForm chainForm) {
        chainService.save(chainForm);
        return new RestResponse("保存成功",ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "getQuery")
    public ChainQuery getQuery(ChainQuery chainQuery){
        return chainQuery;
    }

    @RequestMapping(value = "findOne")
    public ChainDto findOne(String id){
        return chainService.findOne(id);
    }

    @RequestMapping(value = "getForm")
    public ChainForm getForm(ChainForm chainForm){
        return chainForm;
    }

}
