package net.myspring.hr.modules.sys.domain;

import net.myspring.common.domain.DataEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import net.myspring.common.domain.IdEntity;
import java.util.List;
import com.google.common.collect.Lists;

@Entity
@Table(name="sys_town")
public class Town extends IdEntity<Town> {
    private String provinceId;
    private String provinceName;
    private String cityId;
    private String cityName;
    private String countyId;
    private String countyName;
    private String townName;
    private Town town;
    private String townId;
    private List<Town> townList = Lists.newArrayList();
    private List<String> townIdList = Lists.newArrayList();

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyId() {
        return countyId;
    }

    public void setCountyId(String countyId) {
        this.countyId = countyId;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    public String getTownId() {
        return townId;
    }

    public void setTownId(String townId) {
        this.townId = townId;
    }

    public List<Town> getTownList() {
        return townList;
    }

    public void setTownList(List<Town> townList) {
        this.townList = townList;
    }

    public List<String> getTownIdList() {
        return townIdList;
    }

    public void setTownIdList(List<String> townIdList) {
        this.townIdList = townIdList;
    }
}
