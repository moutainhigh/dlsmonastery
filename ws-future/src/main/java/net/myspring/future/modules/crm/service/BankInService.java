package net.myspring.future.modules.crm.service;

import com.google.common.collect.Lists;
import net.myspring.cloud.common.enums.ExtendTypeEnum;
import net.myspring.cloud.modules.input.dto.ArReceiveBillDto;
import net.myspring.cloud.modules.input.dto.ArReceiveBillEntryDto;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.common.enums.SettleTypeEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.basic.client.ActivitiClient;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.domain.Bank;
import net.myspring.future.modules.basic.domain.Client;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.repository.BankRepository;
import net.myspring.future.modules.basic.repository.ClientRepository;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.crm.domain.BankIn;
import net.myspring.future.modules.crm.dto.BankInDto;
import net.myspring.future.modules.crm.repository.BankInRepository;
import net.myspring.future.modules.crm.web.form.BankInAuditForm;
import net.myspring.future.modules.crm.web.form.BankInForm;
import net.myspring.future.modules.crm.web.query.BankInQuery;
import net.myspring.general.modules.sys.dto.ActivitiCompleteDto;
import net.myspring.general.modules.sys.dto.ActivitiStartDto;
import net.myspring.general.modules.sys.form.ActivitiCompleteForm;
import net.myspring.general.modules.sys.form.ActivitiStartForm;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.time.LocalDateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class BankInService {
    @Autowired
    private CloudClient cloudClient;
    @Autowired
    private BankInRepository bankInRepository;
   @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private ActivitiClient activitiClient;

    public Page<BankInDto> findPage(Pageable pageable, BankInQuery bankInQuery) {
        Page<BankInDto> page = bankInRepository.findPage(pageable, bankInQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public void audit(BankInAuditForm bankInAuditForm){

        BankIn bankIn = bankInRepository.findOne(bankInAuditForm.getId());
        ActivitiCompleteDto activitiCompleteDto = activitiClient.complete(new ActivitiCompleteForm(bankIn.getProcessInstanceId(), bankIn.getProcessTypeId(), bankInAuditForm.getAuditRemarks(), bankInAuditForm.getPass()));
        if("已通过".equals(activitiCompleteDto.getProcessStatus())){
            bankIn.setLocked(true);
        }

        bankIn.setProcessFlowId(activitiCompleteDto.getProcessFlowId());
        bankIn.setProcessStatus(activitiCompleteDto.getProcessStatus());
        bankIn.setPositionId(activitiCompleteDto.getPositionId());
        bankIn.setBillDate(bankInAuditForm.getBillDate() == null ? LocalDate.now() : bankInAuditForm.getBillDate());
        bankInRepository.save(bankIn);

        if(Boolean.TRUE.equals(bankInAuditForm.getSyn()) && "已通过".equals(activitiCompleteDto.getProcessStatus())){
            synToCloud(bankIn, bankInAuditForm);
        }
    }

    private void synToCloud(BankIn bankIn, BankInAuditForm bankInAuditForm) {
        Depot depot = depotRepository.findOne(bankIn.getShopId());
        Client client = clientRepository.findOne(depot.getId());

        ArReceiveBillDto receiveBillDto = new ArReceiveBillDto();
        receiveBillDto.setExtendId(bankIn.getId());
        receiveBillDto.setExtendType(ExtendTypeEnum.销售收款.name());
        receiveBillDto.setDate(bankIn.getBillDate());
        receiveBillDto.setCustomerNumber(client.getOutCode());
        ArReceiveBillEntryDto entityDto = new ArReceiveBillEntryDto();
        entityDto.setAmount(bankIn.getAmount());
        if("0".equals(bankIn.getBankId())){
            entityDto.setSettleTypeNumber(SettleTypeEnum.现金.getFNumber());
        }else{
            Bank bank = bankRepository.findOne(bankIn.getBankId());
            entityDto.setBankAcntNumber(bank.getCode());
            entityDto.setSettleTypeNumber(SettleTypeEnum.电汇.getFNumber());
        }
        entityDto.setComment("审："+bankInAuditForm.getAuditRemarks() +"   申："+bankIn.getRemarks());
        receiveBillDto.setArReceiveBillEntryDtoList(Collections.singletonList(entityDto));
        KingdeeSynReturnDto kingdeeSynReturnDto = cloudClient.synReceiveBill(Collections.singletonList(receiveBillDto)).get(0);

        bankIn.setCloudSynId(kingdeeSynReturnDto.getId());
        bankIn.setOutCode(kingdeeSynReturnDto.getBillNo());
        bankInRepository.save(bankIn);
    }

    public BankIn save(BankInForm bankInForm) {
        BankIn bankIn;
        if(bankInForm.isCreate()) {
            bankIn = new BankIn();
        }else{
            bankIn = bankInRepository.findOne(bankInForm.getId());
        }
        bankIn.setShopId(bankInForm.getShopId());
        bankIn.setType(bankInForm.getType());
        bankIn.setBankId(bankInForm.getBankId());
        bankIn.setInputDate(bankInForm.getInputDate());
        bankIn.setAmount(bankInForm.getAmount());
        bankIn.setSerialNumber(bankInForm.getSerialNumber());
        bankIn.setRemarks(bankInForm.getRemarks());
        bankInRepository.save(bankIn);

        if(bankInForm.isCreate()) {
            ActivitiStartDto activitiStartDto = activitiClient.start(new ActivitiStartForm("销售收款",  bankIn.getId(),BankIn.class.getSimpleName(),bankIn.getAmount().toString()));
            bankIn.setProcessFlowId(activitiStartDto.getProcessFlowId());
            bankIn.setProcessStatus(activitiStartDto.getProcessStatus());
            bankIn.setProcessInstanceId(activitiStartDto.getProcessInstanceId());
            bankIn.setPositionId(activitiStartDto.getPositionId());
            bankIn.setProcessTypeId(activitiStartDto.getProcessTypeId());
            bankInRepository.save(bankIn);
        }
        return bankIn;
    }

    public void logicDelete(String id) {
        bankInRepository.logicDelete(id);
    }

    public SimpleExcelBook export(BankInQuery bankInQuery) {

        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "formatId", "编号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "shopName", "门店"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "bankName", "银行"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "amount", "金额"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "serialNumber", "流水号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "billDate", "开单日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "inputDate", "到账日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "createdByName", "创建人"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "createdDate", "创建时间"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "outCode", "外部编号"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "processStatus", "状态"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "remarks", "备注"));

        List<BankInDto> bankInDtoList = findPage(new PageRequest(0,10000), bankInQuery).getContent();
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("销售收款列表", bankInDtoList, simpleExcelColumnList);
        ExcelUtils.doWrite(workbook, simpleExcelSheet);
        return new SimpleExcelBook(workbook,"销售收款列表"+ LocalDateUtils.format(LocalDate.now())+".xlsx",simpleExcelSheet);
    }

    public BankInDto findDto(String id) {
        BankInDto bankInDto = bankInRepository.findDto(id);
        cacheUtils.initCacheInput(bankInDto);
        return bankInDto;
    }
}
