package net.myspring.future.modules.basic.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.modules.sys.dto.AccountCommonDto;
import net.myspring.cloud.modules.kingdee.domain.BdDepartment;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.common.enums.CompanyNameEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.future.common.enums.EmployeePhoneDepositStatusEnum;
import net.myspring.future.common.enums.EmployeePhoneStatusEnum;
import net.myspring.future.common.enums.ShopGoodsDepositStatusEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.AccountClient;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.domain.*;
import net.myspring.future.modules.basic.dto.EmployeePhoneDepositDto;
import net.myspring.future.modules.basic.manager.CnJournalBankManager;
import net.myspring.future.modules.basic.manager.DepotManager;
import net.myspring.future.modules.basic.repository.*;
import net.myspring.future.modules.basic.web.form.EmployeePhoneDepositForm;
import net.myspring.future.modules.basic.web.query.EmployeePhoneDepositQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateTimeUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/17.
 */
@Service
@Transactional(readOnly = true)
public class EmployeePhoneDepositService {

    @Autowired
    private EmployeePhoneDepositRepository employeePhoneDepositRepository;
    @Autowired
    private AccountClient accountClient;
    @Autowired
    private EmployeePhoneRepository employeePhoneRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DepotManager depotManager;
    @Autowired
    private CloudClient cloudClient;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private CnJournalBankManager cnJournalBankManager;

    public EmployeePhoneDepositDto findOne(EmployeePhoneDepositDto employeePhoneDepositDto) {
        if(!employeePhoneDepositDto.isCreate()){
            EmployeePhoneDeposit employeePhoneDeposit = employeePhoneDepositRepository.findOne(employeePhoneDepositDto.getId());
            employeePhoneDepositDto= BeanUtil.map(employeePhoneDeposit,EmployeePhoneDepositDto.class);
            cacheUtils.initCacheInput(employeePhoneDeposit);
        }
        return employeePhoneDepositDto;
    }

    public List<EmployeePhoneDepositDto> findByIds(List<String> ids) {
        List<EmployeePhoneDeposit> employeePhoneDepositList = employeePhoneDepositRepository.findAll(ids);
        List<EmployeePhoneDepositDto> employeePhoneDepositDtoList=BeanUtil.map(employeePhoneDepositList,EmployeePhoneDepositDto.class);
        cacheUtils.initCacheInput(employeePhoneDepositList);
        return employeePhoneDepositDtoList;
    }

    @Transactional
    public EmployeePhoneDeposit save(EmployeePhoneDepositForm employeePhoneDepositForm) {
        EmployeePhoneDeposit employeePhoneDeposit;
        if (employeePhoneDepositForm.isCreate()) {
            String bankName = "GC邮（备用金）";
            if (CompanyNameEnum.JXVIVO.name().equalsIgnoreCase(RequestUtils.getCompanyName())) {
                bankName = "ZBL邮（备用金）";
            }
            employeePhoneDeposit=BeanUtil.map(employeePhoneDepositForm,EmployeePhoneDeposit.class);
            Bank bank = bankRepository.findByName(bankName);
            employeePhoneDeposit.setBankId(bank.getId());
            employeePhoneDeposit.setOutBillType("手工日记账");
            employeePhoneDeposit.setLocked(false);
            employeePhoneDeposit.setStatus(EmployeePhoneDepositStatusEnum.省公司审核.name());
        }else {
            employeePhoneDeposit=employeePhoneDepositRepository.findOne(employeePhoneDepositForm.getId());
            ReflectionUtil.copyProperties(employeePhoneDepositForm,employeePhoneDeposit);
        }
        employeePhoneDepositRepository.save(employeePhoneDeposit);
        return employeePhoneDeposit;
    }

    @Transactional
    public void logicDeleteOne(EmployeePhoneDepositForm employeePhoneDepositForm) {
        employeePhoneDepositRepository.logicDelete(employeePhoneDepositForm.getId());
    }

    public Page<EmployeePhoneDepositDto> findPage(Pageable pageable, EmployeePhoneDepositQuery employeePhoneDepositQuery) {
        Page<EmployeePhoneDepositDto> page = employeePhoneDepositRepository.findPage(pageable, employeePhoneDepositQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    @Transactional
    public void batchAudit(List<String> ids, boolean pass) {


        List<EmployeePhoneDeposit> employeePhoneDepositList=employeePhoneDepositRepository.findAll(ids);
        Map<String,EmployeePhoneDeposit> employeePhoneDepositMap = CollectionUtil.extractToMap(employeePhoneDepositList,"id");
        if (!pass) {
            for(EmployeePhoneDeposit employeePhoneDeposit:employeePhoneDepositList){
                employeePhoneDeposit.setStatus(EmployeePhoneDepositStatusEnum.未通过.name());
            }
            employeePhoneDepositRepository.save(employeePhoneDepositList);
        } else {
            Map<String,Product>  productMap=productRepository.findMap(CollectionUtil.extractToList(employeePhoneDepositList,"productId"));
            for (EmployeePhoneDeposit employeePhoneDeposit : employeePhoneDepositList) {
                if (EmployeePhoneDepositStatusEnum.省公司审核.name().equals(employeePhoneDeposit.getStatus()) && StringUtils.isBlank(employeePhoneDeposit.getOutCode())) {
                    if (StringUtils.isNotBlank(employeePhoneDeposit.getOutBillType()) && "手工日记账".equals(employeePhoneDeposit.getOutBillType())) {
                        employeePhoneDeposit.setStatus(EmployeePhoneDepositStatusEnum.已通过.name());
                        employeePhoneDeposit.setLocked(true);
                        employeePhoneDeposit.setBillDate(LocalDateTime.now());
                        employeePhoneDepositRepository.save(employeePhoneDeposit);
                        if (employeePhoneDeposit.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                            EmployeePhone employeePhone = new EmployeePhone();
                            Product product = productMap.get(employeePhoneDeposit.getProductId());
                            employeePhone.setProductId(employeePhoneDeposit.getProductId());
                            employeePhone.setJobPrice(product.getPrice2());
                            employeePhone.setRetailPrice(product.getRetailPrice());
                            employeePhone.setDepositAmount(employeePhoneDeposit.getAmount());
                            employeePhone.setDepotId(employeePhoneDeposit.getDepotId());
                            employeePhone.setEmployeeId(employeePhoneDeposit.getEmployeeId());
                            employeePhone.setUploadTime(employeePhoneDeposit.getCreatedDate());
                            employeePhone.setStatus(EmployeePhoneStatusEnum.已交.name());
                            employeePhoneRepository.save(employeePhone);
                        }
                    }
                }
            }
            List<KingdeeSynReturnDto> kingdeeSynReturnDtos  = cnJournalBankManager.synEmployeePhoneDeposit(employeePhoneDepositList);
            for (KingdeeSynReturnDto kingdeeSynReturnDto:kingdeeSynReturnDtos){
                EmployeePhoneDeposit employeePhoneDeposit = employeePhoneDepositMap.get(kingdeeSynReturnDto.getExtendId());
                employeePhoneDeposit.setOutCode(kingdeeSynReturnDto.getBillNo());
                employeePhoneDeposit.setCloudSynId(kingdeeSynReturnDto.getId());
                employeePhoneDepositRepository.save(employeePhoneDeposit);
            }
        }
    }

    @Transactional
    public RestResponse batchSave(String data) {
        if (StringUtils.isBlank(data)) {
            return new RestResponse("保存失败，没有任何数据",null);
        }
        List<List<String>> datas = ObjectMapperUtils.readValue(HtmlUtils.htmlUnescape(data), ArrayList.class);
        List<EmployeePhoneDeposit> employeePhoneDeposits = Lists.newArrayList();
        List<BdDepartment> departments = cloudClient.findAllDepartment();
        Map<String, String> departMentMap = Maps.newHashMap();
        for (BdDepartment bdDepartment : departments) {
            departMentMap.put(bdDepartment.getFFullName(), bdDepartment.getFNumber());
        }
        List<String> loginNameList=Lists.newArrayList();
        List<String> depotNameList=Lists.newArrayList();
        List<String> productNameList=Lists.newArrayList();
        for (List<String> row : datas) {
            loginNameList.add(StringUtils.toString(row.get(0)));
            depotNameList.add(StringUtils.toString(row.get(1)));
            productNameList.add(StringUtils.toString(row.get(4)));
        }
        List<Depot> depotList=depotRepository.findByEnabledIsTrueAndNameIn(depotNameList);
        List<Product> productList=productRepository.findByEnabledIsTrueAndNameIn(productNameList);
        List<AccountCommonDto> accountList=accountClient.findByLoginNameList(loginNameList);
        Map<String,Depot> depotMap=CollectionUtil.extractToMap(depotList,"name");
        Map<String,Product> productMap=CollectionUtil.extractToMap(productList,"name");
        Map<String,AccountCommonDto> accountMap=CollectionUtil.extractToMap(accountList,"loginName");
        for (List<String> row : datas) {
            String loginName = StringUtils.toString(row.get(0));
            String depotName = StringUtils.toString(row.get(1));
            String amountStr = StringUtils.toString(row.get(2));
            String department = StringUtils.toString(row.get(3));
            String productName = StringUtils.toString(row.get(4));
            String remarks = StringUtils.toString(row.get(5));
            if (StringUtils.isBlank(loginName) ||StringUtils.isBlank(productName) || StringUtils.isBlank(depotName) || StringUtils.isBlank(amountStr) || StringUtils.isBlank(department)) {
                return new RestResponse("保存失败，员工，门店，收款金额,部门,手机型号不能为空",null);
            }
            BigDecimal amount = new BigDecimal(amountStr);
            Depot depot = depotMap.get(depotName);
            if (depot == null) {
                return new RestResponse("保存失败，门店" + depotName + "在系统中不存在",null);
            }
            String bankName="GC邮（备用金）";
            if(CompanyNameEnum.JXVIVO.name().equalsIgnoreCase(RequestUtils.getCompanyName())){
                bankName="ZBL邮（备用金）";
            }
            EmployeePhoneDeposit employeePhoneDeposit = new EmployeePhoneDeposit();
            AccountCommonDto account=accountMap.get(loginName);
            if(account==null){
                return new RestResponse(account.getLoginName()+"用户名有误，请检查",null);
            }
            Product product=productMap.get(productName);
            if(product==null){
                return new RestResponse(account.getLoginName()+"手机型号有误，请检查",null);
            }
            employeePhoneDeposit.setProductId(product.getId());
            Bank bank=bankRepository.findByName(bankName);
            employeePhoneDeposit.setDepartment(departMentMap.get(department));
            employeePhoneDeposit.setDepotId(depot.getId());
            employeePhoneDeposit.setStatus(ShopGoodsDepositStatusEnum.省公司审核.name());
            employeePhoneDeposit.setOutBillType("手工日记账");
            employeePhoneDeposit.setBankId(bank.getId());
            employeePhoneDeposit.setEmployeeId(account.getEmployeeId());
            employeePhoneDeposit.setAmount(amount);
            employeePhoneDeposit.setRemarks(remarks);
            employeePhoneDeposits.add(employeePhoneDeposit);
        }
        employeePhoneDepositRepository.save(employeePhoneDeposits);
        return new RestResponse("订金收款批量保存成功",null);
    }

    public String export(Workbook workbook, EmployeePhoneDepositQuery employeePhoneDepositQuery){
        employeePhoneDepositQuery.setDepotIdList(depotManager.filterDepotIds(RequestUtils.getAccountId()));
        List<EmployeePhoneDepositDto> employeePhoneDepositDtoList= employeePhoneDepositRepository.findFilter(employeePhoneDepositQuery);
        cacheUtils.initCacheInput(employeePhoneDepositDtoList);
        List<SimpleExcelColumn> simpleExcelColumnList= Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"employeeName","员工姓名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"areaName","办事处"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"depotName","门店"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"department","部门"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"bankName","银行"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"amount","收款金额"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"outCode","外部编号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"billDate","开单时间"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"status","状态"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"productName","手机型号"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("导购用机",employeePhoneDepositDtoList,simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"导购用机"+ LocalDateTimeUtils.format(LocalDateTime.now())+".xlsx",simpleExcelSheet);
        ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
        return null;
    }
}
