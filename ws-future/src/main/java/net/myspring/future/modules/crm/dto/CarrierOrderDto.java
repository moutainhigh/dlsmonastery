package net.myspring.future.modules.crm.dto;

import net.myspring.common.constant.CharConstant;
import net.myspring.common.dto.DataDto;
import net.myspring.future.modules.crm.domain.CarrierOrder;
import net.myspring.util.cahe.annotation.CacheInput;
import net.myspring.util.text.IdUtils;
import net.myspring.util.text.StringUtils;

import java.time.LocalDateTime;

public class CarrierOrderDto extends DataDto<CarrierOrder>{
    private String goodsOrderId;
    private String goodsOrderStatus;
    private String businessId;
    private String areaId;
    private String depotName;
    private String carrieShopName;
    private LocalDateTime shipDate;
    private String code;
    private String status;
    private String formatId;
    @CacheInput(inputKey = "offices",inputInstance = "areaId",outputInstance = "name")
    private String areaName;

    public String getFormatId() {
        if(StringUtils.isNotBlank(businessId)){
            this.formatId= IdUtils.getFormatId(businessId,"XK");
        }
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public String getGoodsOrderId() {
        return goodsOrderId;
    }

    public void setGoodsOrderId(String goodsOrderId) {
        this.goodsOrderId = goodsOrderId;
    }

    public String getGoodsOrderStatus() {
        return goodsOrderStatus;
    }

    public void setGoodsOrderStatus(String goodsOrderStatus) {
        this.goodsOrderStatus = goodsOrderStatus;
    }

    public String getBusinessId() {
        if(StringUtils.isNotBlank(businessId)){
            return businessId.replace(businessId, "XK");
        }
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getCarrieShopName() {
        return carrieShopName;
    }

    public void setCarrieShopName(String carrieShopName) {
        this.carrieShopName = carrieShopName;
    }

    public LocalDateTime getShipDate() {
        return shipDate;
    }

    public void setShipDate(LocalDateTime shipDate) {
        this.shipDate = shipDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}