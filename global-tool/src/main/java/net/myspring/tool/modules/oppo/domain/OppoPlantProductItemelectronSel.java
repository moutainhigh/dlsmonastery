package net.myspring.tool.modules.oppo.domain;


import net.myspring.tool.common.domain.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name="oppo_plant_product_itemelectron_sel")
public class OppoPlantProductItemelectronSel extends IdEntity<OppoPlantProductItemelectronSel> {
    private LocalDateTime createdTime;
    private String customerId;
    private LocalDateTime dateTime;
    private String productNo;
    private String productNob;
    private String remark;
    private String areap;
    private String areac;
    private String imeib;
    private String dlsProductId;
    private String companyName;

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductNob() {
        return productNob;
    }

    public void setProductNob(String productNob) {
        this.productNob = productNob;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAreap() {
        return areap;
    }

    public void setAreap(String areap) {
        this.areap = areap;
    }

    public String getAreac() {
        return areac;
    }

    public void setAreac(String areac) {
        this.areac = areac;
    }

    public String getImeib() {
        return imeib;
    }

    public void setImeib(String imeib) {
        this.imeib = imeib;
    }

    public String getDlsProductId() {
        return dlsProductId;
    }

    public void setDlsProductId(String dlsProductId) {
        this.dlsProductId = dlsProductId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
