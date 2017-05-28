package net.myspring.future.modules.layout.service;

import net.myspring.future.common.enums.AuditStatusEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.domain.PricesystemDetail;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.basic.repository.PricesystemDetailRepository;
import net.myspring.future.modules.layout.domain.ShopAllot;
import net.myspring.future.modules.layout.domain.ShopAllotDetail;
import net.myspring.future.modules.layout.dto.ShopAllotDetailDto;
import net.myspring.future.modules.layout.dto.ShopAllotDto;
import net.myspring.future.modules.layout.repository.ShopAllotDetailRepository;
import net.myspring.future.modules.layout.repository.ShopAllotRepository;
import net.myspring.future.modules.layout.web.form.ShopAllotDetailForm;
import net.myspring.future.modules.layout.web.form.ShopAllotForm;
import net.myspring.future.modules.layout.web.form.ShopAllotViewOrAuditForm;
import net.myspring.future.modules.layout.web.query.ShopAllotQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ShopAllotService {
    @Autowired
    private ShopAllotRepository shopAllotRepository;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private CacheUtils cacheUtils;


    @Autowired
    private ShopAllotDetailRepository shopAllotDetailRepository;
    @Autowired
    private PricesystemDetailRepository pricesystemDetailRepository;


    public ShopAllot findOne(String id){

        return shopAllotRepository.findOne(id);
    }

    public Page<ShopAllotDto> findPage(Pageable pageable, ShopAllotQuery shopAllotQuery) {
        Page<ShopAllotDto> page = shopAllotRepository.findPage(pageable, shopAllotQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public String checkShop(String fromShopId,String toShopId){
        //TODO 需要检查与财务系统关联
        StringBuffer sb = new StringBuffer();
        Depot fromShop = depotRepository.findOne(fromShopId);
        Depot toShop = depotRepository.findOne(toShopId);
        if(fromShopId.equals(toShopId)){
            sb.append("调拨前后门店不能相同");
        }
        return sb.toString();
    }

    public void logicDelete(String id) {
        shopAllotRepository.logicDelete(id);
    }

    public ShopAllotDto findDto(String id) {

        ShopAllotDto result = shopAllotRepository.findShopAllotDto(id);
        cacheUtils.initCacheInput(result);
        return result;
    }

    public ShopAllot save(ShopAllotForm shopAllotForm) {
        ShopAllot shopAllot = null;
        if(shopAllotForm.isCreate()){
            shopAllot = new ShopAllot();
            shopAllot.setFromShopId(shopAllotForm.getFromShopId());
            shopAllot.setToShopId(shopAllotForm.getToShopId());
            shopAllot.setRemarks(shopAllotForm.getRemarks());
            shopAllot.setBusinessId(IdUtils.getNextBusinessId(shopAllotRepository.findMaxBusinessId(LocalDate.now().atStartOfDay())));
            shopAllot.setStatus(AuditStatusEnum.申请中.name());

            shopAllotRepository.save(shopAllot);

            batchSaveShopAllotDetails(shopAllotForm.getShopAllotDetailFormList(), shopAllot);
        }else{
            shopAllot = shopAllotRepository.findOne(shopAllotForm.getId());
            shopAllot.setRemarks( shopAllotForm.getRemarks());
            shopAllotRepository.save(shopAllot);


            batchSaveShopAllotDetails(shopAllotForm.getShopAllotDetailFormList(), shopAllot);
        }

        return shopAllot;
    }

    /**
     * 该方法会首先清空ShopAllot已经关联的ShopAllotDetail记录。之后将传入的门店调拨明细插入ShopAllotDetail表中，并关联至传入的ShopAllot记录
     */
    private void batchSaveShopAllotDetails(List<ShopAllotDetailForm> shopAllotDetailFormList, ShopAllot shopAllot) {
        shopAllotDetailRepository.deleteByShopAllotId(shopAllot.getId());
        if(shopAllotDetailFormList == null || shopAllotDetailFormList.isEmpty()){
            return ;
        }
        Map<String, PricesystemDetail> fromPricesystemMap = CollectionUtil.extractToMap(pricesystemDetailRepository.findByDepotId(shopAllot.getFromShopId()),"productId");
        Map<String, PricesystemDetail> toPricesystemMap = CollectionUtil.extractToMap(pricesystemDetailRepository.findByDepotId(shopAllot.getToShopId()),"productId");

        List<ShopAllotDetail> shopAllotDetailsToBeSaved = new ArrayList<>();
        for(ShopAllotDetailForm shopAllotDetailForm : shopAllotDetailFormList){
            ShopAllotDetail shopAllotDetail = new ShopAllotDetail();
            shopAllotDetail.setId(null);
            shopAllotDetail.setProductId(shopAllotDetailForm.getProductId());
            shopAllotDetail.setQty(shopAllotDetailForm.getQty());
            shopAllotDetail.setShopAllotId(shopAllot.getId());
            shopAllotDetail.setReturnPrice(fromPricesystemMap.get(shopAllotDetail.getProductId()).getPrice());
            shopAllotDetail.setSalePrice(toPricesystemMap.get(shopAllotDetail.getProductId()).getPrice());
            shopAllotDetailsToBeSaved.add(shopAllotDetail);
        }

        shopAllotDetailRepository.save(shopAllotDetailsToBeSaved);

    }

    public void audit(ShopAllotViewOrAuditForm shopAllotViewOrAuditForm) {
//        Account account = AccountUtils.getAccount();
//        if(account.getOutId()==null){
//            return new Message("message_shop_allot_no_finance_message_account",Message.Type.danger);
//        }

        ShopAllot shopAllot = findOne(shopAllotViewOrAuditForm.getId());
        shopAllot.setStatus("1".equals(shopAllotViewOrAuditForm.getPass()) ? AuditStatusEnum.已通过.name() : AuditStatusEnum.未通过.name());
        shopAllot.setAuditRemarks(shopAllotViewOrAuditForm.getAuditRemarks());
        shopAllot.setAuditBy(RequestUtils.getAccountId());
        shopAllot.setAuditDate(LocalDateTime.now());
        shopAllotRepository.save(shopAllot);

        if(!"1".equals(shopAllotViewOrAuditForm.getPass())) {
            return;
        }

        if(shopAllotViewOrAuditForm.getShopAllotDetailList()!=null){
            for(ShopAllotDetailForm each : shopAllotViewOrAuditForm.getShopAllotDetailList()){
                ShopAllotDetail  shopAllotDetail = shopAllotDetailRepository.findOne(each.getId());
                shopAllotDetail.setReturnPrice(each.getReturnPrice());
                shopAllotDetail.setSalePrice(each.getSalePrice());
                shopAllotDetailRepository.save(shopAllotDetail);
            }
        }

        if("1".equals(shopAllotViewOrAuditForm.getSyn())) {
//                Long defaultStoreId = Long.valueOf(Global.getCompanyConfig(shopAllot.getCompany().getId(), CompanyConfigCode.DEFAULT_STORE_ID.getCode()));
//                shopAllot.setStore(depotDao.findOne(defaultStoreId));
//                //都不是寄售门店  一出一退
//                if(shopAllot.getFromShop().getDelegateDepot()==null && shopAllot.getToShop().getDelegateDepot()==null){
//
//                    K3CloudSynEntity outReturnEntity =new K3CloudSynEntity(K3CloudSaveExtend.K3CloudFormId.SAL_RETURNSTOCK.name(),K3CloudSave.K3CloudFormId.AR_receivable.name(), shopAllot.getReturnStock(),shopAllot.getId(),shopAllot.getFormatId(), K3CloudSynEntity.ExtendType.门店调拨.name());
//                    k3cloudSynDao.save(outReturnEntity);
//                    shopAllot.setReturnCloudEntity(outReturnEntity);
//                    //销售开单
//                    K3CloudSynEntity outSaleEntity =new K3CloudSynEntity(K3CloudSaveExtend.K3CloudFormId.SAL_OUTSTOCK.name(),K3CloudSave.K3CloudFormId.AR_receivable.name(), shopAllot.getSaleOutstock(),shopAllot.getId(),shopAllot.getFormatId(), K3CloudSynEntity.ExtendType.门店调拨.name());
//                    k3cloudSynDao.save(outSaleEntity);
//                    shopAllot.setSaleCloudEntity(outSaleEntity);
//                }else{
//                    //都是寄售(直接调拨单)
//                    if(shopAllot.getFromShop().getDelegateDepot()!=null && shopAllot.getToShop().getDelegateDepot() !=null){
//                        K3CloudSynEntity outReturnEntity=new K3CloudSynEntity(K3CloudSave.K3CloudFormId.STK_TransferDirect.name(),shopAllot.getTransferDirect(),shopAllot.getId(),shopAllot.getFormatId(), K3CloudSynEntity.ExtendType.门店调拨.name());
//                        k3cloudSynDao.save(outReturnEntity);
//                        shopAllot.setReturnCloudEntity(outReturnEntity);
//                    }else{
//                        //调前为寄售 出库单
//                        if(shopAllot.getFromShop().getDelegateDepot()!=null){
//                            K3CloudSynEntity outSaleEntity =new K3CloudSynEntity(K3CloudSaveExtend.K3CloudFormId.SAL_OUTSTOCK.name(),K3CloudSave.K3CloudFormId.AR_receivable.name(), shopAllot.getSaleOutstock(),shopAllot.getId(),shopAllot.getFormatId(), K3CloudSynEntity.ExtendType.门店调拨.name());
//                            k3cloudSynDao.save(outSaleEntity);
//                            shopAllot.setSaleCloudEntity(outSaleEntity);
//                        }else{
//                            //调后为寄售  退货单
//                            K3CloudSynEntity outReturnEntity =new K3CloudSynEntity(K3CloudSaveExtend.K3CloudFormId.SAL_RETURNSTOCK.name(),K3CloudSave.K3CloudFormId.AR_receivable.name(), shopAllot.getReturnStock(),shopAllot.getId(),shopAllot.getFormatId(), K3CloudSynEntity.ExtendType.门店调拨.name());
//                            k3cloudSynDao.save(outReturnEntity);
//                            shopAllot.setReturnCloudEntity(outReturnEntity);
//                        }
//                    }
//                }
//                shopAllotDao.save(shopAllot);

//            shopAllotDao.save(shopAllot);
        }

// if(shopAllotAuditForm.getSyn()){
////            k3cloudSynService.syn(shopAllot.getId(), K3CloudSynEntity.ExtendType.门店调拨.name());
//    }

    }

    public List<ShopAllotDetailDto> findDetailListForViewOrAudit(String id) {

        List<ShopAllotDetailDto> result = shopAllotDetailRepository.getShopAllotDetailListForViewOrAudit(id);
        cacheUtils.initCacheInput(result);

        return result;
    }

    public ShopAllotDto findDtoForViewOrAudit(String id) {
        ShopAllotDto result = findDto(id);
        //TODO 如果是申请状态，需要看到两个门店的应收
//        if(AuditType.APPLY.toString().equals(shopAllot.getStatus())) {
//        result.setFromShopShouldGet();
//        result.setToShopShouldGet();
//            shopAllot.getFromShop().setShouldGet(k3cloudService.findShouldGet(company.getOutDbname(),shopAllot.getFromShop().getOutId()));
//            shopAllot.getToShop().setShouldGet(k3cloudService.findShouldGet(company.getOutDbname(),shopAllot.getToShop().getOutId()));
//        }
        return result;
    }
}
