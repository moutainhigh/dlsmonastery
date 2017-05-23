package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import com.mongodb.gridfs.GridFSFile;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.basic.modules.hr.domain.AccountPermission;
import net.myspring.basic.modules.hr.domain.Employee;
import net.myspring.basic.modules.hr.dto.AccountDto;
import net.myspring.basic.modules.hr.dto.EmployeeDto;
import net.myspring.basic.modules.hr.mapper.AccountMapper;
import net.myspring.basic.modules.hr.mapper.AccountPermissionMapper;
import net.myspring.basic.modules.hr.mapper.EmployeeMapper;
import net.myspring.basic.modules.hr.web.form.AccountForm;
import net.myspring.basic.modules.hr.web.query.AccountQuery;
import net.myspring.basic.modules.sys.domain.Permission;
import net.myspring.basic.modules.sys.manager.OfficeManager;
import net.myspring.basic.modules.sys.manager.RoleManager;
import net.myspring.basic.modules.sys.mapper.PermissionMapper;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.response.RestResponse;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by liuj on 2017/3/19.
 */
@Service
public class AccountService {

    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private AccountPermissionMapper accountPermissionMapper;
    @Autowired
    private GridFsTemplate tempGridFsTemplate;
    @Autowired
    private OfficeManager officeManager;

    @Value("${setting.adminIdList}")
    private String adminIdList;

    public Account findOne(String id) {
        Account account = accountMapper.findOne(id);
        return account;
    }

    public AccountDto findOne(AccountDto accountDto) {
        if(!accountDto.isCreate()){
            Account account = accountMapper.findOne(accountDto.getId());
            accountDto = BeanUtil.map(account, AccountDto.class);
            cacheUtils.initCacheInput(accountDto);
        }
        return accountDto;
    }

    public AccountDto findByLoginName(String loginName) {
        Account account = accountMapper.findByLoginName(loginName);
        AccountDto accountDto = BeanUtil.map(account, AccountDto.class);
        cacheUtils.initCacheInput(accountDto);
        return accountDto;
    }

    public Page<AccountDto> findPage(Pageable pageable, AccountQuery accountQuery) {
        Page<AccountDto> accountDtoPage = accountMapper.findPage(pageable, accountQuery);
        cacheUtils.initCacheInput(accountDtoPage.getContent());
        return accountDtoPage;
    }

    public List<AccountDto> findByFilter(AccountQuery accountQuery) {
        List<Account> accountList = accountMapper.findByFilter(accountQuery);
        List<AccountDto> accountDtoList = BeanUtil.map(accountList, AccountDto.class);
        cacheUtils.initCacheInput(accountList);
        return accountDtoList;
    }

    public Account save(AccountForm accountForm) {
        Account account;
        if (accountForm.isCreate()) {
            accountForm.setPassword(StringUtils.getEncryptPassword(accountForm.getLoginName()));
            account = BeanUtil.map(accountForm, Account.class);
            accountMapper.save(account);
        } else {
            if (StringUtils.isNotBlank(accountForm.getPassword())) {
                accountForm.setPassword(StringUtils.getEncryptPassword(accountForm.getPassword()));
            } else {
                accountForm.setPassword(accountMapper.findOne(accountForm.getId()).getPassword());
            }
            account = accountMapper.findOne(accountForm.getId());
            ReflectionUtil.copyProperties(accountForm,account);
            accountMapper.update(account);
        }
        if ("主账号".equals(accountForm.getType())) {
            Employee employee=employeeMapper.findOne(accountForm.getEmployeeId());
            employee.setAccountId(account.getId());
            employeeMapper.update(employee);
        }
        return account;
    }


    public void logicDeleteOne(String id) {
        accountMapper.logicDeleteOne(id);
    }

    public List<AccountDto> findByLoginNameLikeAndType(Map<String, Object> map) {
        List<Account> accountList = accountMapper.findByLoginNameLikeAndType(map);
        List<AccountDto> accountDtoList = BeanUtil.map(accountList, AccountDto.class);
        return accountDtoList;
    }

    public List<String> getAuthorityList() {
        String accountId= RequestUtils.getAccountId();
        String roleId = roleManager.findIdByAccountId(accountId);
        List<String> authorityList;
        List<Permission> permissionList;
        if(StringUtils.getSplitList(adminIdList, CharConstant.COMMA).contains(RequestUtils.getAccountId())){
            permissionList=permissionMapper.findAllEnabled();
        }else {
            List<String> accountPermissions=accountPermissionMapper.findPermissionIdByAccount(accountId);
            if(CollectionUtil.isNotEmpty(accountPermissions)){
                permissionList=permissionMapper.findByRoleAndAccount(roleId,accountId);
            }else {
                permissionList=permissionMapper.findByRoleId(roleId);
            }
        }
        authorityList= CollectionUtil.extractToList(permissionList,"permission");
        return authorityList;
    }

    public AccountDto getAccountDto(String accountId){
        AccountDto accountDto=BeanUtil.map(accountMapper.findOne(accountId),AccountDto.class);
        cacheUtils.initCacheInput(accountDto);
        return accountDto;
    }


    public String findSimpleExcelSheet(Workbook workbook,AccountQuery accountQuery) throws IOException {
        accountQuery.setOfficeIds(officeManager.officeFilter(RequestUtils.getRequestEntity().getOfficeId()));
        List<Account> accountList = accountMapper.findByFilter(accountQuery);
        List<AccountDto> accountDtoList = BeanUtil.map(accountList, AccountDto.class);
        cacheUtils.initCacheInput(accountDtoList);
        List<SimpleExcelColumn> simpleExcelColumnList=Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "type", "类型"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "loginName", "登录名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "employeeName", "姓名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "leaderName", "上级"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "officeName", "部门"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "employeeStatus", "是否在职"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "entryDate", "入职日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "regularDate", "转正日期"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "leaveDate", "离职日期"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("账户信息模版",accountDtoList,simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"账户信息模版"+ UUID.randomUUID()+".xlsx",simpleExcelSheet);
        ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
        GridFSFile gridFSFile = tempGridFsTemplate.store(byteArrayInputStream,simpleExcelBook.getName(),"application/octet-stream; charset=utf-8", RequestUtils.getDbObject());
        return StringUtils.toString(gridFSFile.getId());
    }

    public void saveAccountAndPermission(AccountForm accountForm){
        List<String> permissionIdList=accountPermissionMapper.findPermissionIdByAccount(accountForm.getId());
        List<String>removeIdList=CollectionUtil.subtract(permissionIdList,accountForm.getPermissionIdList());
        List<String> addIdList=CollectionUtil.subtract(accountForm.getPermissionIdList(),permissionIdList);
        List<AccountPermission> accountPermissions=Lists.newArrayList();
        if(CollectionUtil.isNotEmpty(removeIdList)){
            accountPermissionMapper.removeByPermissionList(removeIdList);
        }
        if(CollectionUtil.isNotEmpty(addIdList)){
            for(String permissionId:addIdList){
                accountPermissions.add(new AccountPermission(accountForm.getId(), permissionId));
            }
            accountPermissionMapper.batchSave(accountPermissions);
        }
    }

    @Transactional(readOnly = true)
    public List<AccountDto> findByIds(List<String> ids){
        List<Account> districts = accountMapper.findByIds(ids);
        List<AccountDto> districtDtos= BeanUtil.map(districts,AccountDto.class);
        return districtDtos;
    }


    public Boolean checkLoginName(AccountQuery accountQuery){
        Account account = accountMapper.findByLoginName(accountQuery.getLoginName());
        return account == null || (account.getId().equals(accountQuery.getId()));
    }

}
