package net.myspring.cloud.modules.sys.domain;


import net.myspring.cloud.common.domain.CompanyEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="sys_kingdee_book")
public class KingdeeBook extends CompanyEntity<KingdeeBook> {
    private String name;
    private String type;
    private String kingdeeUrl;
    private String kingdeePostUrl;
    private String kingdeeUsername;
    private String kingdeePassword;
    private String kingdeeDbid;
    private Integer version = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKingdeeUrl() {
        return kingdeeUrl;
    }

    public void setKingdeeUrl(String kingdeeUrl) {
        this.kingdeeUrl = kingdeeUrl;
    }

    public String getKingdeePostUrl() {
        return kingdeePostUrl;
    }

    public void setKingdeePostUrl(String kingdeePostUrl) {
        this.kingdeePostUrl = kingdeePostUrl;
    }

    public String getKingdeeUsername() {
        return kingdeeUsername;
    }

    public void setKingdeeUsername(String kingdeeUsername) {
        this.kingdeeUsername = kingdeeUsername;
    }

    public String getKingdeePassword() {
        return kingdeePassword;
    }

    public void setKingdeePassword(String kingdeePassword) {
        this.kingdeePassword = kingdeePassword;
    }

    public String getKingdeeDbid() {
        return kingdeeDbid;
    }

    public void setKingdeeDbid(String kingdeeDbid) {
        this.kingdeeDbid = kingdeeDbid;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
