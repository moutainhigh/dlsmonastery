package net.myspring.hr.modules.sys.domain;

import net.myspring.common.domain.DataEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import net.myspring.common.domain.IdEntity;
import java.util.List;
import com.google.common.collect.Lists;
import net.myspring.hr.modules.hr.domain.AccountChange;
import net.myspring.hr.modules.hr.domain.AuditFile;
import net.myspring.hr.modules.hr.domain.EmployeeSalaryBasic;
import net.myspring.hr.modules.hr.domain.OfficeChange;
import net.myspring.hr.modules.hr.domain.Position;

@Entity
@Table(name="sys_process_flow")
public class ProcessFlow extends IdEntity<ProcessFlow> {
    private String name;
    private Integer sort;
    private List<AccountChange> accountChangeList = Lists.newArrayList();
    private List<String> accountChangeIdList = Lists.newArrayList();
    private List<AuditFile> auditFileList = Lists.newArrayList();
    private List<String> auditFileIdList = Lists.newArrayList();
    private List<EmployeeSalaryBasic> employeeSalaryBasicList = Lists.newArrayList();
    private List<String> employeeSalaryBasicIdList = Lists.newArrayList();
    private List<OfficeChange> officeChangeList = Lists.newArrayList();
    private List<String> officeChangeIdList = Lists.newArrayList();
    private Position position;
    private String positionId;
    private ProcessType processType;
    private String processTypeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<AccountChange> getAccountChangeList() {
        return accountChangeList;
    }

    public void setAccountChangeList(List<AccountChange> accountChangeList) {
        this.accountChangeList = accountChangeList;
    }

    public List<String> getAccountChangeIdList() {
        return accountChangeIdList;
    }

    public void setAccountChangeIdList(List<String> accountChangeIdList) {
        this.accountChangeIdList = accountChangeIdList;
    }

    public List<AuditFile> getAuditFileList() {
        return auditFileList;
    }

    public void setAuditFileList(List<AuditFile> auditFileList) {
        this.auditFileList = auditFileList;
    }

    public List<String> getAuditFileIdList() {
        return auditFileIdList;
    }

    public void setAuditFileIdList(List<String> auditFileIdList) {
        this.auditFileIdList = auditFileIdList;
    }

    public List<EmployeeSalaryBasic> getEmployeeSalaryBasicList() {
        return employeeSalaryBasicList;
    }

    public void setEmployeeSalaryBasicList(List<EmployeeSalaryBasic> employeeSalaryBasicList) {
        this.employeeSalaryBasicList = employeeSalaryBasicList;
    }

    public List<String> getEmployeeSalaryBasicIdList() {
        return employeeSalaryBasicIdList;
    }

    public void setEmployeeSalaryBasicIdList(List<String> employeeSalaryBasicIdList) {
        this.employeeSalaryBasicIdList = employeeSalaryBasicIdList;
    }

    public List<OfficeChange> getOfficeChangeList() {
        return officeChangeList;
    }

    public void setOfficeChangeList(List<OfficeChange> officeChangeList) {
        this.officeChangeList = officeChangeList;
    }

    public List<String> getOfficeChangeIdList() {
        return officeChangeIdList;
    }

    public void setOfficeChangeIdList(List<String> officeChangeIdList) {
        this.officeChangeIdList = officeChangeIdList;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    public String getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(String processTypeId) {
        this.processTypeId = processTypeId;
    }
}
