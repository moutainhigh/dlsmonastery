package net.myspring.basic.modules.sys.web.controller;

import com.google.common.collect.Lists;
import net.myspring.basic.modules.sys.domain.Backend;
import net.myspring.basic.modules.sys.dto.BackendDto;
import net.myspring.basic.modules.sys.dto.DictEnumDto;
import net.myspring.basic.modules.sys.web.form.BackendForm;
import net.myspring.basic.modules.sys.web.form.DictEnumForm;
import net.myspring.basic.modules.sys.web.query.BackendQuery;
import net.myspring.basic.modules.sys.web.query.DictEnumQuery;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.text.MD5Utils;
import net.myspring.util.text.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import net.myspring.basic.modules.sys.service.BackendService;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "sys/backend")
public class BackendController {

    @Autowired
    private BackendService backendService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasPermission(null,'sys:backend:view')")
    public Page<BackendDto> list(Pageable pageable, BackendQuery  backendQuery){
        Page<BackendDto> page = backendService.findPage(pageable,backendQuery);
        return page;
    }

    @RequestMapping(value = "delete")
    @PreAuthorize("hasPermission(null,'sys:backend:delete')")
    public RestResponse delete(String id) {
        backendService.logicDelete(id);
        return new RestResponse("删除成功", ResponseCodeEnum.removed.name());
    }

    @RequestMapping(value = "save")
    @PreAuthorize("hasPermission(null,'sys:backend:edit')")
    public RestResponse save(BackendForm backendForm) {
        backendService.save(backendForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "findOne")
    public BackendDto findOne(BackendDto backendDto){
        backendDto=backendService.findOne(backendDto);
        return backendDto;
    }

    @RequestMapping(value = "search")
    public List<BackendDto> search(String name){
        List<BackendDto> backendDtoList= Lists.newArrayList();
        if(StringUtils.isNotBlank(name)){
            backendDtoList=backendService.findByNameLike(name);
        }
        return backendDtoList;
    }

    @RequestMapping(value = "getQuery")
    public BackendQuery getQuery(BackendQuery backendQuery){
        return backendQuery;
    }

    @RequestMapping(value = "getForm")
    public BackendForm getQuery(BackendForm backendForm){
        return backendForm;
    }
}
