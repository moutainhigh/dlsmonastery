package net.myspring.future.modules.basic.service;


import com.google.common.collect.Lists;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.domain.EmployeePhone;
import net.myspring.future.modules.basic.dto.EmployeePhoneDto;
import net.myspring.future.modules.basic.manager.DepotManager;
import net.myspring.future.modules.basic.repository.EmployeePhoneRepository;
import net.myspring.future.modules.basic.web.form.EmployeePhoneForm;
import net.myspring.future.modules.basic.web.query.EmployeePhoneQuery;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateTimeUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EmployeePhoneService {

    @Autowired
    private EmployeePhoneRepository employeePhoneRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private DepotManager depotManager;

    public EmployeePhoneDto findOne(EmployeePhoneDto employeePhoneDto) {
        if(!employeePhoneDto.isCreate()){
            EmployeePhone employeePhone = employeePhoneRepository.findOne(employeePhoneDto.getId());
            employeePhoneDto= BeanUtil.map(employeePhone,EmployeePhoneDto.class);
            cacheUtils.initCacheInput(employeePhoneDto);
        }
        return employeePhoneDto;
    }

    @Transactional
    public EmployeePhone save(EmployeePhoneForm employeePhoneForm) {
        EmployeePhone oldEmployeePhone = employeePhoneRepository.findOne(employeePhoneForm.getId());
        if (oldEmployeePhone.getProductId() == null || StringUtils.isEmpty(oldEmployeePhone.getImeStr())) {
            ReflectionUtil.copyProperties(employeePhoneForm,oldEmployeePhone);
            oldEmployeePhone.setReconditionAmount(new BigDecimal(300));
            employeePhoneRepository.save(oldEmployeePhone);
        } else {
            oldEmployeePhone.setEnabled(false);
            employeePhoneRepository.save(oldEmployeePhone);
            EmployeePhone newEmployeePhone = BeanUtil.map(employeePhoneForm,EmployeePhone.class);
            newEmployeePhone.setUploadTime(oldEmployeePhone.getUploadTime());
            employeePhoneRepository.save(newEmployeePhone);
        }
        return oldEmployeePhone;
    }

    @Transactional
    public void logicDeleteOne(EmployeePhoneForm employeePhoneForm) {
        employeePhoneRepository.logicDelete(employeePhoneForm.getId());
    }

    public Page<EmployeePhoneDto> findPage(Pageable pageable, EmployeePhoneQuery employeePhoneQuery) {
        Page<EmployeePhoneDto> page = employeePhoneRepository.findPage(pageable, employeePhoneQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public SimpleExcelBook findSimpleExcelSheet(EmployeePhoneQuery employeePhoneQuery)  {
        Workbook workbook = new SXSSFWorkbook(10000);
        employeePhoneQuery.setDepotIdList(depotManager.filterDepotIds(RequestUtils.getAccountId()));
        List<EmployeePhoneDto> employeePhoneDtoList = findPage(new PageRequest(0,10000),employeePhoneQuery).getContent();
        cacheUtils.initCacheInput(employeePhoneDtoList);
        List<SimpleExcelColumn> simpleExcelColumnList= Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"uploadTime","申请日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"areaName","办事处"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"depotName","门店"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"employeeName","使用人姓名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"depositAmount","押金"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"status","押金状态"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"jobPrice","批发价"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"retailPrice","零售价"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"productTypeName","货品型号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"productName","货品机型"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"imeStr","串码"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("导购用机",employeePhoneDtoList,simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"导购用机"+ LocalDateTimeUtils.format(LocalDateTime.now())+".xlsx",simpleExcelSheet);
        ExcelUtils.doWrite(workbook,simpleExcelSheet);
        return simpleExcelBook;
    }
}
