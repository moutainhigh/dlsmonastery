package net.myspring.future.modules.crm.web.controller;

import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.basic.modules.sys.dto.CompanyConfigCacheDto;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.CarrierOrderStatusEnum;
import net.myspring.future.common.enums.GoodsOrderStatusEnum;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.crm.domain.CarrierOrder;
import net.myspring.future.modules.crm.domain.GoodsOrder;
import net.myspring.future.modules.crm.dto.CarrierOrderDto;
import net.myspring.future.modules.crm.service.CarrierOrderService;
import net.myspring.future.modules.crm.service.GoodsOrderService;
import net.myspring.future.modules.crm.service.GoodsOrderShipService;
import net.myspring.future.modules.crm.web.form.CarrierOrderFrom;
import net.myspring.future.modules.crm.web.form.GoodsOrderShipForm;
import net.myspring.future.modules.crm.web.query.CarrierOrderQuery;
import net.myspring.util.excel.ExcelView;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.ServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "api/carrierOrder")
public class CarrierOrderController {

    @Autowired
    private CarrierOrderService carrierOrderService;
    @Autowired
    private GoodsOrderService goodsOrderService;
    @Autowired
    private GoodsOrderShipService goodsOrderShipService;

    @RequestMapping
    public Page<CarrierOrderDto> list(Pageable pageable,CarrierOrderQuery carrierOrderQuery) {
        Page<CarrierOrderDto> carrierOrderDtoPage=carrierOrderService.findPage(pageable,carrierOrderQuery);
        return carrierOrderDtoPage;
    }

    @RequestMapping(value = "getQuery")
    public CarrierOrderQuery getQuery(CarrierOrderQuery carrierOrderQuery) {
        carrierOrderQuery.getExtra().put("carrierOrderStatusList",CarrierOrderStatusEnum.getList());
        carrierOrderQuery.getExtra().put("goodsOrderStatusList",GoodsOrderStatusEnum.getList());
        return carrierOrderQuery;
    }

    @RequestMapping(value = "checkBusinessId")
    public RestResponse checkBusinessId(CarrierOrderDto carrierOrderDto){
        RestResponse restResponse=new RestResponse("验证成功",null);
        if(StringUtils.isNotBlank(carrierOrderDto.getBusinessId())){
            GoodsOrder goodsOrder = goodsOrderService.findByBusinessId(carrierOrderDto.getBusinessId());
            if(goodsOrder==null){
                restResponse=new RestResponse("不存在该订单",null,false);
                return restResponse;
            }
            carrierOrderDto.setGoodsOrderId(goodsOrder.getId());
        }
        return restResponse;
    }

    @RequestMapping(value = "save")
    public RestResponse save(CarrierOrderFrom carrierOrderFrom) {
        RestResponse restResponse=new RestResponse("保存成功",null);
        Map<String, Object> map =carrierOrderService.checkDetailJsons(carrierOrderFrom);
        if (!map.isEmpty() && map.get("message") != null && StringUtils.isNotBlank(map.get("message").toString())) {
            String message = map.get("message").toString();
            restResponse=new RestResponse(message,null);
        } else {
            carrierOrderService.save(carrierOrderFrom);
        }
        return restResponse;
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(String id) {
        carrierOrderService.delete(id);
        return new RestResponse("删除成功",null);
    }

    @SuppressWarnings("unchecked")
    @PreAuthorize("hasPermission(null,'api:carrierOrder:ship')")
    public RestResponse carrierShip(String data){
        List<List<String>> datas = ObjectMapperUtils.readValue(HtmlUtils.htmlUnescape(data), ArrayList.class);
        Map<String,Object> map=carrierOrderService.carrierShipCheck(datas);
        if(map.get("message")!=null&&StringUtils.isNotBlank(map.get("message").toString())){
            return new RestResponse(map.get("message").toString(),null,false);
        }else{
            List<GoodsOrderShipForm> goodsOrderShipForms=(List<GoodsOrderShipForm>) map.get("goodsOrderShipForms");
            StringBuilder sb=new StringBuilder();
            for(GoodsOrderShipForm goodsOrderShipForm:goodsOrderShipForms) {
                Map<String,Object> shipCheckMap = goodsOrderShipService.shipCheck(goodsOrderShipForm);
                if(StringUtils.isNotBlank(shipCheckMap.get("errMsg").toString())) {
                    sb.append(shipCheckMap.get("errMsg").toString());
                }
            }
            if(StringUtils.isNotBlank(sb.toString())) {
                return new RestResponse(sb.toString(),null,false);
            }
            for(GoodsOrderShipForm goodsOrderShipForm:goodsOrderShipForms){
                goodsOrderShipService.ship(goodsOrderShipForm);
            }
            return new RestResponse("货品批量发货成功",null);
        }
    }

    @RequestMapping(value="export",method = RequestMethod.GET)
    public ModelAndView export(CarrierOrderQuery carrierOrderQuery){
        SimpleExcelBook simpleExcelSheet = carrierOrderService.findSimpleExcelSheet(carrierOrderQuery);
        ExcelView excelView = new ExcelView();
        return new ModelAndView(excelView, "simpleExcelBook", simpleExcelSheet);
    }
}