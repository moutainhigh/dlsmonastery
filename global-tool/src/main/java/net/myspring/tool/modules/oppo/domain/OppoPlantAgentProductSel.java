package net.myspring.tool.modules.oppo.domain;

import net.myspring.tool.common.domain.IdEntity;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="oppo_plant_agent_product_sel")
public class OppoPlantAgentProductSel extends IdEntity<OppoPlantAgentProductSel> {
    private String brandId;
    private String brandName;
    private String typeId;
    private String typeName;
    private String colorId;
    private String colorName;
    private String codeRecordType;
    private String brandType;
    private String itemDesc;
    private String itemNumber;
    private String lxProductId;
    private String productId;
    private String companyName;

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getColorId() {
        return colorId;
    }

    public void setColorId(String colorId) {
        this.colorId = colorId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getCodeRecordType() {
        return codeRecordType;
    }

    public void setCodeRecordType(String codeRecordType) {
        this.codeRecordType = codeRecordType;
    }

    public String getBrandType() {
        return brandType;
    }

    public void setBrandType(String brandType) {
        this.brandType = brandType;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getLxProductId() {
        return lxProductId;
    }

    public void setLxProductId(String lxProductId) {
        this.lxProductId = lxProductId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
