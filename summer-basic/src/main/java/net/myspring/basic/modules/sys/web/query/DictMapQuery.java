package net.myspring.basic.modules.sys.web.query;

import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateTimeUtils;

import java.time.LocalDateTime;

/**
 * Created by lihx on 2017/4/7.
 */
public class DictMapQuery {
    private String category;
    private String name;
    private String value;
    private String createdDateBTW;
    private LocalDateTime createdDateStart;
    private LocalDateTime createdDateEnd;

    public LocalDateTime getCreatedDateStart() {
        return createdDateStart;
    }

    public void setCreatedDateStart(LocalDateTime createdDateStart) {
        if(createdDateEnd==null&& StringUtils.isNotBlank(createdDateBTW)){
            String[] tempParamValues = createdDateBTW.split(" - ");
            this.createdDateStart= LocalDateTimeUtils.parse(tempParamValues[0]);
        }
        this.createdDateStart = createdDateStart;
    }

    public LocalDateTime getCreatedDateEnd() {
        if(createdDateEnd==null&& StringUtils.isNotBlank(createdDateBTW)){
            String[] tempParamValues = createdDateBTW.split(" - ");
            this.createdDateEnd= LocalDateTimeUtils.parse(tempParamValues[1]+ " 23:59:59");
        }
        return createdDateEnd;
    }

    public void setCreatedDateEnd(LocalDateTime createdDateEnd) {
        this.createdDateEnd = createdDateEnd;
    }

    public String getCreatedDateBTW() {
        return createdDateBTW;
    }

    public void setCreatedDateBTW(String createdDateBTW) {
        this.createdDateBTW = createdDateBTW;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
