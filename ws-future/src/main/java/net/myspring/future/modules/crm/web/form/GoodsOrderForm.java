package net.myspring.future.modules.crm.web.form;

import com.google.common.collect.Lists;
import net.myspring.common.form.DataForm;
import net.myspring.future.modules.crm.domain.GoodsOrder;

import java.util.List;

public class GoodsOrderForm extends DataForm<GoodsOrder> {

    private String shopId;
    private String netType;
    private String shipType;
    private Boolean isUseTicket;

    private List<String> netTypeList;
    private List<String> shipTypeList;

    private List<GoodsOrderDetailForm> goodsOrderDetailList = Lists.newArrayList();

    public Boolean getUseTicket() {
        return isUseTicket;
    }

    public void setUseTicket(Boolean useTicket) {
        isUseTicket = useTicket;
    }

    public List<GoodsOrderDetailForm> getGoodsOrderDetailList() {
        return goodsOrderDetailList;
    }

    public void setGoodsOrderDetailList(List<GoodsOrderDetailForm> goodsOrderDetailList) {
        this.goodsOrderDetailList = goodsOrderDetailList;
    }

    public Boolean getIsUseTicket() {
        return isUseTicket;
    }

    public void setIsUseTicket(Boolean isUseTicket) {
        this.isUseTicket = isUseTicket;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public List<String> getNetTypeList() {
        return netTypeList;
    }

    public void setNetTypeList(List<String> netTypeList) {
        this.netTypeList = netTypeList;
    }

    public List<String> getShipTypeList() {
        return shipTypeList;
    }

    public void setShipTypeList(List<String> shipTypeList) {
        this.shipTypeList = shipTypeList;
    }

}