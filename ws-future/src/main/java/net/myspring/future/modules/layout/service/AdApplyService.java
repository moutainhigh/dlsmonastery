package net.myspring.future.modules.layout.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.basic.modules.sys.dto.DictEnumDto;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.future.common.enums.BillTypeEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.dto.ProductDto;
import net.myspring.future.modules.basic.repository.ProductRepository;
import net.myspring.future.modules.basic.web.form.ProductAdForm;
import net.myspring.future.modules.layout.domain.AdApply;
import net.myspring.future.modules.layout.dto.AdApplyDto;
import net.myspring.future.modules.layout.repository.AdApplyRepository;
import net.myspring.future.modules.layout.web.form.AdApplyBillForm;
import net.myspring.future.modules.layout.web.form.AdApplyForm;
import net.myspring.future.modules.layout.web.query.AdApplyQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.IdUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdApplyService {
    @Autowired
    private AdApplyRepository adApplyRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private RedisTemplate redisTemplate;

    public Page<AdApplyDto> findPage(Pageable pageable, AdApplyQuery adApplyQuery) {
        Page<AdApplyDto> page = adApplyRepository.findPage(pageable, adApplyQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public AdApplyDto findOne(String id){
        AdApplyDto adApplyDto;
        if(StringUtils.isBlank(id)){
            adApplyDto = new AdApplyDto();
        } else {
            AdApply adApply= adApplyRepository.findOne(id);
            adApplyDto = BeanUtil.map(adApply,AdApplyDto.class);
            cacheUtils.initCacheInput(adApplyDto);
        }
        return adApplyDto;
    }

    public AdApplyForm getForm(AdApplyForm adApplyForm){
        List<String> billTypes = new ArrayList<>();
        billTypes.add(BillTypeEnum.POP.name());
        billTypes.add(BillTypeEnum.配件赠品.name());
        adApplyForm.getExtra().put("billTypes",billTypes);
        return adApplyForm;
    }

    public AdApplyBillForm getBillForm(AdApplyBillForm adApplyBillForm){
        List<String> billTypes = new ArrayList<>();
        billTypes.add(BillTypeEnum.POP.name());
        billTypes.add(BillTypeEnum.配件赠品.name());
        adApplyBillForm.getExtra().put("billTypes",billTypes);
        if(adApplyBillForm.getBillType().equalsIgnoreCase(BillTypeEnum.POP.name())){
            adApplyBillForm.setStoreId(CompanyConfigUtil.findByCode(redisTemplate,RequestUtils.getCompanyId(),CompanyConfigCodeEnum.AD_DEFAULT_STORE_ID.name()).getValue());
        }
        if(adApplyBillForm.getBillType().equalsIgnoreCase(BillTypeEnum.配件赠品.name())){
            adApplyBillForm.setStoreId(CompanyConfigUtil.findByCode(redisTemplate,RequestUtils.getCompanyId(),CompanyConfigCodeEnum.DEFAULT_STORE_ID.name()).getValue());
        }
        return adApplyBillForm;
    }

    public List<AdApplyDto> findAdApplyList(String billType){
        List<String> outGroupIds = Lists.newArrayList();
        if(BillTypeEnum.POP.name().equalsIgnoreCase(billType)){
            outGroupIds = IdUtils.getIdList(CompanyConfigUtil.findByCode(redisTemplate, RequestUtils.getCompanyId(), CompanyConfigCodeEnum.PRODUCT_POP_GROUP_IDS.name()).getValue());
        }
        if(BillTypeEnum.配件赠品.name().equalsIgnoreCase(billType)){
            outGroupIds = IdUtils.getIdList(CompanyConfigUtil.findByCode(redisTemplate, RequestUtils.getCompanyId(), CompanyConfigCodeEnum.PRODUCT_GOODS_POP_GROUP_IDS.name()).getValue());
        }
        LocalDate dateStart = LocalDate.now().plusYears(-1);
        List<AdApplyDto> adApplyDtos = adApplyRepository.findByOutGroupIdAndDate(dateStart,outGroupIds);
        cacheUtils.initCacheInput(adApplyDtos);
        return adApplyDtos;
    }

    public List<AdApply> findAdApplyGoodsList(){
        Map<String,Object> filter = Maps.newHashMap();
        filter.put("adShop",true);
        List<String> adApplyIdList = adApplyRepository.findAllId();
        List<AdApply> adApplys = Lists.newArrayList();

        return adApplys;
    }

    public void save(AdApplyForm adApplyForm){
        if(CollectionUtil.isEmpty(adApplyForm.getProductAdForms())){
            return;
        }
        List<AdApply> adApplyList = Lists.newArrayList();
        for(ProductAdForm productAdForm:adApplyForm.getProductAdForms()){
            Integer applyQty = productAdForm.getApplyQty();
            if(applyQty!=null&&applyQty>0){
                AdApply adApply = new AdApply();
                adApply.setApplyQty(applyQty);
                adApply.setConfirmQty(applyQty);
                adApply.setBilledQty(0);
                adApply.setLeftQty(applyQty);
                adApply.setShopId(adApplyForm.getShopId());
                adApply.setProductId(productAdForm.getId());
                adApply.setRemarks(adApplyForm.getRemarks());
                adApply.setExpiryDateRemarks(productAdForm.getExpiryDateRemarks());
                adApplyList.add(adApply);
            }
        }
        adApplyRepository.save(adApplyList);
    }
}
