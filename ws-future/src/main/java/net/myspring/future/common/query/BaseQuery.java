package net.myspring.future.common.query;


import com.google.common.collect.Maps;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by liuj on 2017/5/9.
 */
public class BaseQuery {
    private String companyId= RequestUtils.getCompanyId();

    private List<String> officeIdList;

    private List<String> depotIdList;

    private  Integer page = 0;

    private Integer size = 50;

    private String sort = "id,DESC";

    private Map<String,Object> extra = Maps.newHashMap();

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public List<String> getOfficeIdList() {
        if(CollectionUtil.isEmpty(officeIdList)){
            this.officeIdList=RequestUtils.getOfficeIdList();
        }
        return officeIdList;
    }

    public void setOfficeIdList(List<String> officeIdList) {
        this.officeIdList = officeIdList;
    }

    public List<String> getDepotIdList() {
        return depotIdList;
    }

    public void setDepotIdList(List<String> depotIdList) {
        this.depotIdList = depotIdList;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
