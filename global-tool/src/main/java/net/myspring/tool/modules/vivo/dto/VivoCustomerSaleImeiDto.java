package net.myspring.tool.modules.vivo.dto;

import net.myspring.util.cahe.annotation.CacheInput;

public class VivoCustomerSaleImeiDto {
    private String provinceId;
    private String imei;
    private String productId;
    private String shopId;
    private String saleTime;
    private String customerName;
    private String customerMobile;
    private String customerSex;
    private String salePromoter;
    private String shopCode;
    private String shopName;
    @CacheInput(inputKey = "offices",inputInstance = "provinceId" ,outputInstance = "agentCode")
    private String agentCode;

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(String saleTime) {
        this.saleTime = saleTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getCustomerSex() {
        return customerSex;
    }

    public void setCustomerSex(String customerSex) {
        this.customerSex = customerSex;
    }

    public String getSalePromoter() {
        return salePromoter;
    }

    public void setSalePromoter(String salePromoter) {
        this.salePromoter = salePromoter;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }
}
