package net.myspring.future.modules.crm.domain;

import net.myspring.future.common.domain.IdEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="crm_report_score_office")
public class ReportScoreOffice extends IdEntity<ReportScoreOffice> {
    private LocalDate scoreDate;
    private BigDecimal score;
    private BigDecimal monthScore;
    private Integer saleQty=0;
    private Integer monthSaleQty=0;
    private Integer recentMonthSaleQty=0;
    private Integer dateRank;
    private Integer monthRank;
    private BigDecimal saleMoney=BigDecimal.ZERO;
    private BigDecimal monthSaleMoney=BigDecimal.ZERO;
    private BigDecimal recentMonthSaleMoney=BigDecimal.ZERO;
    private String officeId;
    private String areaId;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    private String reportScoreAreaId;

    public LocalDate getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(LocalDate scoreDate) {
        this.scoreDate = scoreDate;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getMonthScore() {
        return monthScore;
    }

    public void setMonthScore(BigDecimal monthScore) {
        this.monthScore = monthScore;
    }

    public Integer getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(Integer saleQty) {
        this.saleQty = saleQty;
    }

    public Integer getMonthSaleQty() {
        return monthSaleQty;
    }

    public void setMonthSaleQty(Integer monthSaleQty) {
        this.monthSaleQty = monthSaleQty;
    }

    public Integer getRecentMonthSaleQty() {
        return recentMonthSaleQty;
    }

    public void setRecentMonthSaleQty(Integer recentMonthSaleQty) {
        this.recentMonthSaleQty = recentMonthSaleQty;
    }

    public Integer getDateRank() {
        return dateRank;
    }

    public void setDateRank(Integer dateRank) {
        this.dateRank = dateRank;
    }

    public Integer getMonthRank() {
        return monthRank;
    }

    public void setMonthRank(Integer monthRank) {
        this.monthRank = monthRank;
    }

    public BigDecimal getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(BigDecimal saleMoney) {
        this.saleMoney = saleMoney;
    }

    public BigDecimal getMonthSaleMoney() {
        return monthSaleMoney;
    }

    public void setMonthSaleMoney(BigDecimal monthSaleMoney) {
        this.monthSaleMoney = monthSaleMoney;
    }

    public BigDecimal getRecentMonthSaleMoney() {
        return recentMonthSaleMoney;
    }

    public void setRecentMonthSaleMoney(BigDecimal recentMonthSaleMoney) {
        this.recentMonthSaleMoney = recentMonthSaleMoney;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getReportScoreAreaId() {
        return reportScoreAreaId;
    }

    public void setReportScoreAreaId(String reportScoreAreaId) {
        this.reportScoreAreaId = reportScoreAreaId;
    }
}
