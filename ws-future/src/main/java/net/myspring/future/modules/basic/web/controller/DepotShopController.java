package net.myspring.future.modules.basic.web.controller;

import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.TownTypeEnum;
import net.myspring.future.modules.basic.domain.DepotShop;
import net.myspring.future.modules.basic.dto.AdPricesystemDto;
import net.myspring.future.modules.basic.dto.ChainDto;
import net.myspring.future.modules.basic.dto.DepotShopDto;
import net.myspring.future.modules.basic.dto.PricesystemDto;
import net.myspring.future.modules.basic.service.AdPricesystemService;
import net.myspring.future.modules.basic.service.ChainService;
import net.myspring.future.modules.basic.service.DepotShopService;
import net.myspring.future.modules.basic.service.PricesystemService;
import net.myspring.future.modules.basic.web.form.DepotForm;
import net.myspring.future.modules.basic.web.form.DepotShopForm;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuj on 2017/5/12.
 */
@RestController
@RequestMapping(value = "basic/depotShop")
public class DepotShopController {

    @Autowired
    private DepotShopService depotShopService;
    @Autowired
    private ChainService chainService;
    @Autowired
    private AdPricesystemService adPricesystemService;
    @Autowired
    private PricesystemService pricesystemService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<DepotShopDto> list(Pageable pageable, DepotQuery depotShopQuery){
        Page<DepotShopDto> page = depotShopService.findPage(pageable,depotShopQuery);
        return page;
    }

    @RequestMapping(value = "save")
    public RestResponse list(DepotShopForm depotShopForm){
        depotShopService.save(depotShopForm);
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "saveDepot")
    public RestResponse saveDepot(DepotForm depotForm){
        depotShopService.saveDepot(depotForm);
        return new RestResponse("保存成功",null);
    }

    @RequestMapping(value = "getForm")
    public DepotShopForm getForm(DepotShopForm depotShopForm){
        depotShopForm = depotShopService.getForm(depotShopForm);
        depotShopForm.setTownTypeList(TownTypeEnum.getList());
        return depotShopForm;
    }

    @RequestMapping(value = "findDepotForm")
    public DepotForm findDepotForm(DepotForm depotForm){
        depotForm = depotShopService.findDepotForm(depotForm);
        depotForm.setAdPricesystemList(adPricesystemService.findAllEnabled());
        depotForm.setPricesystemList(pricesystemService.findAllEnabled());
        depotForm.setChainList(chainService.findAllEnabled());
        return depotForm;
    }
}