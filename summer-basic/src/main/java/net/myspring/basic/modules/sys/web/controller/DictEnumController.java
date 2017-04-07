package net.myspring.basic.modules.sys.web.controller;

import com.google.common.collect.Maps;
import net.myspring.basic.modules.sys.dto.DictEnumDto;
import net.myspring.basic.modules.sys.service.DictEnumService;
import net.myspring.basic.modules.sys.web.form.DictEnumForm;
import net.myspring.basic.modules.sys.web.query.DictEnumQuery;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.json.ObjectMapperUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "sys/dictEnum")
public class DictEnumController {

    @Autowired
    private DictEnumService dictEnumService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<DictEnumDto>  list( Pageable pageable, DictEnumQuery dictEnumQuery){
        Page<DictEnumDto> page = dictEnumService.findPage(pageable,dictEnumQuery);
        return page;
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id) {
        dictEnumService.logicDeleteOne(id);
        RestResponse restResponse =new RestResponse("删除成功",ResponseCodeEnum.removed.name());
        return restResponse;
    }

    @RequestMapping(value = "save")
    public RestResponse save(DictEnumForm dictEnumForm) {
        dictEnumService.save(dictEnumForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }

    @RequestMapping(value = "findOne")
    public DictEnumDto findOne(String id){
        DictEnumDto dictEnumDto=dictEnumService.findDto(id);
        return dictEnumDto;
    }

    @RequestMapping(value="getFormProperty")
    public Map<String,Object> getFormProperty(){
        Map<String,Object> map= Maps.newHashMap();
        map.put("category", dictEnumService.findDistinctCategory());
        return map;
    }

    @RequestMapping(value="getListProperty")
    public Map<String,Object> getListProperty(){
        Map<String,Object> map= Maps.newHashMap();
        map.put("category", dictEnumService.findDistinctCategory());
        return map;
    }
}
