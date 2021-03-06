package net.myspring.cloud.modules.input.web.form;


import com.google.common.collect.Lists;
import net.myspring.common.form.BaseForm;

import java.time.LocalDate;
import java.util.List;

/**
 * 采购入库
 * Created by lihx on 2017/6/13.
 */
public class StkInStockForm extends BaseForm<StkInStockForm> {
    private LocalDate billDate;
    private String stockNumber;
    private String departmentNumber;
    private String supplierNumber;
    private String json;

    //附带属性
    private List<String> typeList = Lists.newArrayList();

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(String stockNumber) {
        this.stockNumber = stockNumber;
    }

    public String getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

}
