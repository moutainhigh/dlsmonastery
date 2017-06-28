package net.myspring.future.modules.layout.service;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.future.common.enums.TotalPriceTypeEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.basic.client.ActivitiClient;
import net.myspring.future.modules.basic.domain.ShopAdType;
import net.myspring.future.modules.basic.repository.ShopAdTypeRepository;
import net.myspring.future.modules.layout.domain.ShopAd;
import net.myspring.future.modules.layout.dto.ShopAdDto;
import net.myspring.future.modules.layout.repository.ShopAdRepository;
import net.myspring.future.modules.layout.web.form.ShopAdAuditForm;
import net.myspring.future.modules.layout.web.form.ShopAdForm;
import net.myspring.future.modules.layout.web.query.ShopAdQuery;
import net.myspring.general.modules.sys.dto.ActivitiCompleteDto;
import net.myspring.general.modules.sys.dto.ActivitiStartDto;
import net.myspring.general.modules.sys.form.ActivitiCompleteForm;
import net.myspring.general.modules.sys.form.ActivitiStartForm;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ShopAdService {

    @Autowired
    private ShopAdRepository shopAdRepository;
    @Autowired
    private ShopAdTypeRepository shopAdTypeRepository;
    @Autowired
    private ActivitiClient activitiClient;
    @Autowired
    private CacheUtils cacheUtils;

    public Page<ShopAdDto> findPage(Pageable pageable, ShopAdQuery shopAdQuery) {
        Page<ShopAdDto> page = shopAdRepository.findPage(pageable, shopAdQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public ShopAd save(ShopAdForm shopAdForm) {
        ShopAd shopAd;
        ShopAdType shopAdType = shopAdTypeRepository.findOne(shopAdForm.getShopAdTypeId());
        BigDecimal price=BigDecimal.ZERO;
        if(TotalPriceTypeEnum.按数量.toString().equals(shopAdType.getTotalPriceType())){
            price=shopAdType.getPrice().multiply(new BigDecimal(shopAdForm.getQty()));
        }
        if(TotalPriceTypeEnum.按面积.toString().equals(shopAdType.getTotalPriceType())){
            price=shopAdForm.getLength().multiply(shopAdForm.getWidth()).multiply(shopAdType.getPrice()).multiply(new BigDecimal(shopAdForm.getQty()));
        }
        price = price.setScale(0,BigDecimal.ROUND_HALF_UP);
        shopAdForm.setPrice(price);

        if(shopAdForm.isCreate()){
            shopAd = BeanUtil.map(shopAdForm,ShopAd.class);
            shopAdRepository.save(shopAd);
            //启动流程
            ActivitiStartDto activitiStartDto = activitiClient.start(new ActivitiStartForm("广告申请",shopAd.getId(),ShopAd.class.getSimpleName(),shopAdForm.getPrice().toString()));
            if(activitiStartDto!=null){
                shopAd.setProcessPositionId(activitiStartDto.getPositionId());
                shopAd.setProcessStatus(activitiStartDto.getProcessStatus());
                shopAd.setProcessFlowId(activitiStartDto.getProcessFlowId());
                shopAd.setProcessInstanceId(activitiStartDto.getProcessInstanceId());
                shopAd.setProcessTypeId(activitiStartDto.getProcessTypeId());
                shopAdRepository.save(shopAd);
            }
        }else {
            shopAd = shopAdRepository.findOne(shopAdForm.getId());
            ReflectionUtil.copyProperties(shopAdForm,shopAd);
            shopAdRepository.save(shopAd);
        }
        return shopAd;
    }

    public ShopAdQuery getQuery(ShopAdQuery shopAdQuery) {
        shopAdQuery.getExtra().put("shopAdTypes",shopAdTypeRepository.findAllByEnabled());
        return shopAdQuery;
    }


    public String audit(ShopAdAuditForm shopAdAuditForm) {
        ActivitiCompleteForm activitiCompleteForm = new ActivitiCompleteForm();
        ShopAd shopAd;
        if(!shopAdAuditForm.isCreate()){
            shopAd = shopAdRepository.findOne(shopAdAuditForm.getId());
            activitiCompleteForm.setProcessInstanceId(shopAd.getProcessInstanceId());
            activitiCompleteForm.setProcessTypeId(shopAd.getProcessTypeId());
            activitiCompleteForm.setComment(shopAdAuditForm.getPassRemarks());
            activitiCompleteForm.setPass(shopAdAuditForm.getPass());
            try {
                ActivitiCompleteDto activitiCompleteDto = activitiClient.complete(activitiCompleteForm);

                if(activitiCompleteDto!=null){
                    shopAd.setLocked(true);
                    shopAd.setProcessFlowId(activitiCompleteDto.getProcessFlowId());
                    shopAd.setProcessStatus(activitiCompleteDto.getProcessStatus());
                    shopAd.setProcessPositionId(activitiCompleteDto.getPositionId());
                    shopAdRepository.save(shopAd);
                    return null;
                }
            }catch (Exception e){
                return e.getMessage();
            }
        }
        return null;
    }

    public String batchAudit(String[] ids, Boolean pass){
        if(ids==null){
            return null;
        }
        List<String> idList = Arrays.asList(ids);
        ShopAdAuditForm shopAdAuditForm = new ShopAdAuditForm();
        shopAdAuditForm.setPass(pass);
        shopAdAuditForm.setPassRemarks("批量操作");
        String message = null;
        for (String id:idList){
            shopAdAuditForm.setId(id);
            message = audit(shopAdAuditForm);
            if(message!=null){
                message += StringUtils.join(message, CharConstant.COMMA);
            }
        }
        return message;
    }

    public ShopAdDto findOne(String id) {
        ShopAdDto shopAdDto = new ShopAdDto();
        if(StringUtils.isNotBlank(id)){
            ShopAd shopAd = shopAdRepository.findOne(id);
            shopAdDto = BeanUtil.map(shopAd,ShopAdDto.class);
            cacheUtils.initCacheInput(shopAdDto);
        }
        return shopAdDto;
    }

    public ShopAdForm getForm(ShopAdForm shopAdForm){
        shopAdForm.getExtra().put("shopAdTypeFormList",shopAdTypeRepository.findAllByEnabled());
        return shopAdForm;
    }

    public void logicDelete(String id) {
        shopAdRepository.logicDelete(id);
    }

    public String findSimpleExcelSheets(Workbook workbook, ShopAdQuery shopAdQuery) throws IOException{

        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();

        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "shopName", "门店"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "shopAdTypeName", "广告品种"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "length", "长度"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "width", "宽度"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "qty", "数量"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "specialArea", "是否专区"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "content", "内容说明"));

        List<ShopAdDto> shopAdDtoList = shopAdRepository.findByFilter(shopAdQuery);
        cacheUtils.initCacheInput(shopAdDtoList);
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("广告申请", shopAdDtoList, simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"广告申请"+ UUID.randomUUID()+".xlsx",simpleExcelSheet);
        ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
                return null;
    }
}
