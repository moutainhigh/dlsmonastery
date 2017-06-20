package net.myspring.common.query;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by liuj on 2017/5/9.
 */
public class BaseQuery {

    private  Integer page = 0;

    private Integer size = 50;

    protected String sort = "id,DESC";

    private Map<String,Object> extra = Maps.newHashMap();


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