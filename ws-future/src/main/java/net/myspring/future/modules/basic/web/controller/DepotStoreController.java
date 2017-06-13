package net.myspring.future.modules.basic.web.controller;

import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.basic.modules.sys.dto.CompanyConfigCacheDto;
import net.myspring.common.enums.BoolEnum;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.*;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.domain.DepotStore;
import net.myspring.future.modules.basic.dto.DepotShopDto;
import net.myspring.future.modules.basic.dto.DepotStoreDto;
import net.myspring.future.modules.basic.service.DepotShopService;
import net.myspring.future.modules.basic.service.DepotStoreService;
import net.myspring.future.modules.basic.web.form.DepotStoreForm;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.future.modules.basic.web.query.DepotStoreQuery;
import net.myspring.future.modules.crm.web.query.ReportQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuj on 2017/5/12.
 */
@RestController
@RequestMapping(value = "basic/depotStore")
public class DepotStoreController {

    @Autowired
    private DepotStoreService depotStoreService;
    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(method = RequestMethod.GET)
    public Page<DepotStoreDto> list(Pageable pageable, DepotStoreQuery depotStoreQuery){
        Page<DepotStoreDto> page = depotStoreService.findPage(pageable,depotStoreQuery);
        return page;
    }

    @RequestMapping(value = "getForm")
    public DepotStoreForm getForm(DepotStoreForm depotStoreForm){
        depotStoreForm=depotStoreService.getForm(depotStoreForm);
        depotStoreForm.setDepotStoreTypeList(DepotStoreTypeEnum.getList());
        return depotStoreForm;
    }

    @RequestMapping(value = "save")
    public RestResponse save(DepotStoreForm depotStoreForm){
        depotStoreService.save(depotStoreForm);
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(DepotStoreForm depotStoreForm){
        depotStoreService.logicDelete(depotStoreForm.getId());
        return new RestResponse("删除成功",null);
    }

    @RequestMapping(value = "getDepotStoreQuery")
    public DepotStoreQuery getDepotStoreQuery(DepotStoreQuery depotStoreQuery){
        depotStoreQuery.getExtra().put("typeList", ReportTypeEnum.getList());
        depotStoreQuery.getExtra().put("outTypeList", OutTypeEnum.getList());
        depotStoreQuery.getExtra().put("boolMap", BoolEnum.getMap());
        CompanyConfigCacheDto companyConfigCacheDto = CompanyConfigUtil.findByCode( redisTemplate, RequestUtils.getCompanyId(), CompanyConfigCodeEnum.PRODUCT_NAME.name());
        if(companyConfigCacheDto != null && "WZOPPO".equals(companyConfigCacheDto.getValue())) {
            depotStoreQuery.setOutType(ProductImeStockReportOutTypeEnum.核销.name());
        }else{
            depotStoreQuery.setOutType(ProductImeStockReportOutTypeEnum.电子保卡.name());
        }
        depotStoreQuery.setType(ReportTypeEnum.核销.name());
        depotStoreQuery.setScoreType(true);
        return depotStoreQuery;
    }


}
