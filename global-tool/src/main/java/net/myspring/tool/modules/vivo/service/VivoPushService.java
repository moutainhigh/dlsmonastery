package net.myspring.tool.modules.vivo.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.common.enums.CompanyNameEnum;
import net.myspring.tool.common.client.OfficeClient;
import net.myspring.tool.common.dataSource.DbContextHolder;
import net.myspring.tool.common.dataSource.annotation.FactoryDataSource;
import net.myspring.tool.common.dataSource.annotation.LocalDataSource;
import net.myspring.tool.modules.future.dto.OfficeDto;
import net.myspring.tool.common.utils.CacheUtils;
import net.myspring.tool.modules.vivo.domain.*;
import net.myspring.tool.modules.vivo.dto.*;
import net.myspring.tool.modules.vivo.repository.*;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateTimeUtils;
import net.myspring.util.time.LocalDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@LocalDataSource
public class VivoPushService {

    @Autowired
    private OfficeClient officeClient;
    @Autowired
    private SZonesRepository sZonesRepository;
    @Autowired
    private SCustomersRepository sCustomersRepository;
    @Autowired
    private VivoPlantProductsRepository vivoPlantProductsRepository;
    @Autowired
    private SPlantStockStoresRepository sPlantStockStoresRepository;
    @Autowired
    private SPlantStockSupplyRepository sPlantStockSupplyRepository;
    @Autowired
    private SPlantStockDealerRepository sPlantStockDealerRepository;
    @Autowired
    private SProductItemStocksRepository sProductItemStocksRepository;
    @Autowired
    private SProductItem000Repository sProductItem000Repository;
    @Autowired
    private SProductItemLendRepository sProductItemLendRepository;
    @Autowired
    private SPlantEndProductSaleRepository sPlantEndProductSaleRepository;
    @Autowired
    private SStoresRepository sStoresRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private RedisTemplate redisTemplate;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Transactional
    public void pushToLocal(PushToLocalDto pushToLocalDto,String companyName){
        //同步机构数据
        pushVivoZonesData(companyName);
        //客户数据
        pushVivoPushSCustomersData(pushToLocalDto.getsCustomerDtoList(),pushToLocalDto.getDate());
        //库存汇总数据
        pushCustomerStockData(pushToLocalDto.getsPlantCustomerStockDtoList(),pushToLocalDto.getProductColorMap(),pushToLocalDto.getDate());
        //库存串码明细
        pushCustomerStockDetailData(pushToLocalDto.getsPlantCustomerStockDetailDtoList(),pushToLocalDto.getProductColorMap(),pushToLocalDto.getDate());
        //演示机数据
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            pushDemoPhonesData(pushToLocalDto.getsProductItemLendList(),pushToLocalDto.getProductColorMap(),pushToLocalDto.getDate());
        }
        //核销记录数据
        pushProductImeSaleData(pushToLocalDto.getVivoCustomerSaleImeiDtoList(),pushToLocalDto.getProductColorMap(),pushToLocalDto.getDate());
        //一代仓库上抛
        pushSStoreData(pushToLocalDto.getsStoresList());
    }



    @Transactional
    public  List<SZones> pushVivoZonesData(String companyName){
        logger.info("机构数据同步开始"+ LocalDateTime.now());
        List<OfficeDto> officeDtoList = officeClient.findAllChildCount(companyName);
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
            for (OfficeDto officeDto : officeDtoList){
                officeDto.setAgentCode(mainCode);
            }
        }
        List<SZones> sZonesList = Lists.newArrayList();
        for(OfficeDto officeDto:officeDtoList){
            SZones sZones = new SZones();
            sZones.setZoneId(getZoneId(officeDto.getAgentCode(),officeDto.getId()));
            sZones.setZoneName(officeDto.getName());
            sZones.setShortcut(officeDto.getAgentCode());
            String[] parentIds = officeDto.getParentIds().split(CharConstant.COMMA);
            sZones.setZoneDepth(parentIds.length);
            StringBuilder zonePath = new StringBuilder(CharConstant.VERTICAL_LINE);
            for(String parentId:parentIds){
                zonePath.append(getZoneId(officeDto.getAgentCode(),parentId)).append(CharConstant.VERTICAL_LINE);
            }
            zonePath.append(getZoneId(officeDto.getAgentCode(),officeDto.getId())).append(CharConstant.VERTICAL_LINE);
            sZones.setZonePath(zonePath.toString());
            sZones.setFatherId(getZoneId(officeDto.getAgentCode(),officeDto.getParentId()));
            sZones.setSubCount(officeDto.getChildCount());
            sZones.setZoneTypes(CharConstant.EMPTY);
            sZones.setAgentCode(officeDto.getAgentCode());
            sZonesList.add(sZones);
        }
        sZonesRepository.deleteAll();
        sZonesRepository.batchSave(sZonesList);
        logger.info("机构数据同步完成"+LocalDateTime.now());
        return sZonesList;
    }


    @Transactional
    public void pushVivoPushSCustomersData(List<SCustomerDto> futureCustomerDtoList,String date){
        logger.info("客户数据同步开始"+LocalDateTime.now());
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
            for (SCustomerDto sCustomerDto : futureCustomerDtoList){
                sCustomerDto.setAgentCode(mainCode);
            }
        }
        List<SCustomers> sCustomersList = Lists.newArrayList();
        for(SCustomerDto futureCustomerDto :futureCustomerDtoList){
            SCustomers sCustomers = new SCustomers();
            String customerId = futureCustomerDto.getCustomerId();
            String agentCode = futureCustomerDto.getAgentCode();
            sCustomers.setCustomerLevel(futureCustomerDto.getCustomerLevel());
            if(futureCustomerDto.getCustomerLevel() == 1){
                sCustomers.setCustomerId(StringUtils.getFormatId(customerId,agentCode+"D","00000"));
                sCustomers.setCustomerName(futureCustomerDto.getAreaName());
            }else {
                sCustomers.setCustomerId(StringUtils.getFormatId(customerId,agentCode+"C","00000"));
                sCustomers.setCustomerName(futureCustomerDto.getCustomerName());
                sCustomers.setCustomerStr4(StringUtils.getFormatId(futureCustomerDto.getCustomerStr4(),futureCustomerDto.getAgentCode()+"D","00000"));
            }
            if("R250082".equals(agentCode)){
                sCustomers.setCustomerStr4(agentCode + "K0000");
            }
            sCustomers.setZoneId(getZoneId(agentCode,futureCustomerDto.getZoneId()));
            sCustomers.setCompanyId(agentCode);
            sCustomers.setRecordDate(futureCustomerDto.getRecordDate());
            sCustomers.setCustomerStr1(futureCustomerDto.getCustomerStr1());
            sCustomers.setCustomerStr10(agentCode);
            sCustomers.setAgentCode(agentCode);
            sCustomersList.add(sCustomers);
        }
        sCustomersRepository.deleteAll();
        sCustomersRepository.batchSave(sCustomersList);
        logger.info("客户数据同步完成"+LocalDateTime.now());
    }


    @Transactional
    public void pushCustomerStockData(List<SPlantCustomerStockDto> sPlantCustomerStockDtoList,Map<String,String> productColorMap,String date){
        logger.info("一代、二代、经销商库存数据同步开始:"+LocalDateTime.now());
        String dateStart = LocalDateUtils.format(LocalDateUtils.parse(date));
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(dateStart));
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
            for (SPlantCustomerStockDto sPlantCustomerStockDto : sPlantCustomerStockDtoList){
                sPlantCustomerStockDto.setAgentCode(mainCode);
            }
        }

        List<SPlantStockStores> sPlantStockStoresList = Lists.newArrayList();
        List<SPlantStockSupply> sPlantStockSupplyList = Lists.newArrayList();
        List<SPlantStockDealer> sPlantStockDealerList = Lists.newArrayList();

        for (SPlantCustomerStockDto sPlantCustomerStockDto : sPlantCustomerStockDtoList) {
            String colorId = productColorMap.get(sPlantCustomerStockDto.getProductId());
            if (StringUtils.isBlank(colorId)){
                continue;
            }
            int customerLevel = sPlantCustomerStockDto.getCustomerLevel();
            String agentCode=sPlantCustomerStockDto.getAgentCode();
            if (customerLevel == 1) {
                SPlantStockStores sPlantStockStores = new SPlantStockStores();
                sPlantStockStores.setCompanyId(agentCode);
                sPlantStockStores.setStoreId(agentCode+"K0000");
                sPlantStockStores.setProductId(colorId);
                sPlantStockStores.setSumStock(0);
                sPlantStockStores.setUseAbleStock(sPlantCustomerStockDto.getUseAbleStock());
                sPlantStockStores.setBad(0);
                sPlantStockStores.setCreatedTime(LocalDateTimeUtils.format(LocalDateTime.now()));
                sPlantStockStores.setAccountDate(dateStart);
                sPlantStockStores.setAgentCode(agentCode);
                sPlantStockStoresList.add(sPlantStockStores);
            }
            if (customerLevel == 2) {
                String supplyId = StringUtils.getFormatId(sPlantCustomerStockDto.getCustomerId(),"D","00000");
                SPlantStockSupply sPlantStockSupply = new SPlantStockSupply();
                sPlantStockSupply.setCompanyId(agentCode);
                sPlantStockSupply.setSupplyId(supplyId);
                sPlantStockSupply.setProductId(colorId);
                sPlantStockSupply.setCreatedTime(LocalDateTimeUtils.format(LocalDateTime.now()));
                sPlantStockSupply.setSumStock(0);
                sPlantStockSupply.setUseAbleStock(sPlantCustomerStockDto.getUseAbleStock());
                sPlantStockSupply.setBad(0);
                sPlantStockSupply.setAccountDate(dateStart);
                sPlantStockSupply.setAgentCode(agentCode);
                sPlantStockSupplyList.add(sPlantStockSupply);
            }
            if (customerLevel == 3) {
                String dealerId = StringUtils.getFormatId(sPlantCustomerStockDto.getCustomerId(),"C","00000");
                SPlantStockDealer sPlantStockDealer = new SPlantStockDealer();
                sPlantStockDealer.setCompanyId(agentCode);
                sPlantStockDealer.setDealerId(dealerId);
                sPlantStockDealer.setProductId(colorId);
                sPlantStockDealer.setSumStock(0);
                sPlantStockDealer.setUseAbleStock(sPlantCustomerStockDto.getUseAbleStock());
                sPlantStockDealer.setBad(0);
                sPlantStockDealer.setCreatedTime(LocalDateTimeUtils.format(LocalDateTime.now()));
                sPlantStockDealer.setAccountDate(dateStart);
                sPlantStockDealer.setAgentCode(agentCode);
                sPlantStockDealerList.add(sPlantStockDealer);
            }
        }

        logger.info("一代库库存数据同步开始"+LocalDateTime.now());
        sPlantStockStoresRepository.deleteByAccountDate(dateStart,dateEnd);
        sPlantStockStoresRepository.batchSave(sPlantStockStoresList);
        logger.info("一代库库存数据同步完成"+LocalDateTime.now());

        logger.info("二代库库存数据同步开始"+LocalDateTime.now());
        sPlantStockSupplyRepository.deleteByAccountDate(dateStart,dateEnd);
        sPlantStockSupplyRepository.batchSave(sPlantStockSupplyList);
        logger.info("二代库库存数据同步完成"+LocalDateTime.now());

        logger.info("经销商库存数据同步开始"+LocalDateTime.now());
        sPlantStockDealerRepository.deleteByAccountDate(dateStart,dateEnd);
        sPlantStockDealerRepository.batchSave(sPlantStockDealerList);
        logger.info("经销商库存数据同步完成"+LocalDateTime.now());
        logger.info("一代、二代、经销商库存数据同步完成:"+LocalDateTime.now());
    }

    @Transactional
    public void pushCustomerStockDetailData(List<SPlantCustomerStockDetailDto> sPlantCustomerStockDetailDtoList,Map<String,String> productColorMap,String date){
        logger.info("库存串码明细数据同步开始:"+LocalDateTime.now());
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
            for (SPlantCustomerStockDetailDto sPlantCustomerStockDetailDto : sPlantCustomerStockDetailDtoList){
                sPlantCustomerStockDetailDto.setAgentCode(mainCode);
            }
        }

        List<SProductItemStocks> sProductItemStocksList = Lists.newArrayList();
        List<SProductItem000> sProductItem000List = Lists.newArrayList();

        for (SPlantCustomerStockDetailDto sPlantCustomerStockDetailDto : sPlantCustomerStockDetailDtoList){
            int customerLevel = sPlantCustomerStockDetailDto.getCustomerLevel();
            String colorId = productColorMap.get(sPlantCustomerStockDetailDto.getProductId());
            if(StringUtils.isBlank(colorId)){
                continue;
            }
            String agentCode = sPlantCustomerStockDetailDto.getAgentCode();
            String productNo = sPlantCustomerStockDetailDto.getIme();
            if (customerLevel == 1){
                SProductItemStocks sProductItemStocks = new SProductItemStocks();
                sProductItemStocks.setCompanyId(agentCode);
                sProductItemStocks.setProductId(colorId);
                sProductItemStocks.setProductNo(productNo);
                sProductItemStocks.setStoreId(agentCode+"K0000");
                sProductItemStocks.setStatus("在渠道中");
                sProductItemStocks.setStatusInfo(agentCode+"K0000");
                sProductItemStocks.setAgentCode(agentCode);
                sProductItemStocks.setUpdateTime(date);
                sProductItemStocksList.add(sProductItemStocks);
            } else {
                SProductItem000 sProductItem000 = new SProductItem000();
                sProductItem000.setCompanyId(agentCode);
                sProductItem000.setProductId(colorId);
                sProductItem000.setProductNo(productNo);
                sProductItem000.setAgentCode(agentCode);
                if(customerLevel == 2){
                    if("R250082".equals(agentCode)) {
                        sProductItem000.setStoreId(agentCode + "K0000");
                    }else{
                        sProductItem000.setStoreId(StringUtils.getFormatId(sPlantCustomerStockDetailDto.getStoreId(), agentCode + "D", "00000"));
                    }
                    sProductItem000.setStatusInfo(sProductItem000.getStoreId());
                }
                if(customerLevel==3){
                    if("R250082".equals(agentCode)) {
                        sProductItem000.setStoreId(agentCode + "K0000");
                    }else{
                        sProductItem000.setStoreId(StringUtils.getFormatId(sPlantCustomerStockDetailDto.getStoreId(), agentCode + "D", "00000"));
                    }
                    sProductItem000.setStatusInfo(StringUtils.getFormatId(sPlantCustomerStockDetailDto.getCustomerId(), agentCode + "C", "00000"));
                }
                sProductItem000.setStatus("在渠道中");
                sProductItem000.setUpdateTime(date);
                sProductItem000List.add(sProductItem000);
            }
        }
        sProductItemStocksRepository.deleteByUpdateTime(date,LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1)));
        sProductItemStocksRepository.batchSave(sProductItemStocksList);
        sProductItem000Repository.deleteByUpdateTime(date,LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1)));
        sProductItem000Repository.batchSave(sProductItem000List);
        logger.info("库存串码明细数据同步完成:"+LocalDateTime.now());
    }

    @Transactional
    public void pushDemoPhonesData(List<SProductItemLend> sProductItemLendList, Map<String,String> productColorMap, String date){
        logger.info("借机数据同步开始:"+LocalDateTime.now());
        String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
        if (StringUtils.isBlank(date)){
            date = LocalDateUtils.format(LocalDate.now());
        }
        String dateStart = date;
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<SProductItemLend> sProductItemLends = Lists.newArrayList();

        for (SProductItemLend sProductItemLend : sProductItemLendList){
            String productId=sProductItemLend.getProductID();
            if (StringUtils.isBlank(productColorMap.get(productId))){
                continue;
            }
            sProductItemLend.setStatusInfo(StringUtils.getFormatId(sProductItemLend.getCompanyID(),"C","00000"));
            sProductItemLend.setCompanyID(mainCode);
            sProductItemLend.setProductID(productColorMap.get(productId));
            sProductItemLend.setStoreID(mainCode+"K0000");
            sProductItemLend.setStatus("借机出库");
            sProductItemLends.add(sProductItemLend);
        }

        sProductItemLendRepository.deleteByUpdateTime(dateStart,dateEnd);
        sProductItemLendRepository.batchSave(sProductItemLends);
        logger.info("借机数据同步完成:"+LocalDateTime.now());
    }

    @Transactional
    public void pushProductImeSaleData(List<VivoCustomerSaleImeiDto> vivoCustomerSaleImeiDtoList,Map<String,String> productColorMap,String date){
        logger.info("核销记录数据同步开始:"+LocalDateTime.now());
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
            for (VivoCustomerSaleImeiDto vivoCustomerSaleImeiDto : vivoCustomerSaleImeiDtoList){
                vivoCustomerSaleImeiDto.setAgentCode(mainCode);
            }
        }
        if (StringUtils.isBlank(date)){
            date = LocalDateUtils.format(LocalDate.now());
        }
        String dateStart = date;
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<SPlantEndProductSale> sPlantEndProductSaleList = Lists.newArrayList();
        for (VivoCustomerSaleImeiDto vivoCustomerSaleImeiDto : vivoCustomerSaleImeiDtoList){
            if (StringUtils.isBlank(productColorMap.get(vivoCustomerSaleImeiDto.getProductId()))){
                continue;
            }
            String agentCode=vivoCustomerSaleImeiDto.getAgentCode();
            SPlantEndProductSale sPlantEndProductSale = new SPlantEndProductSale();
            sPlantEndProductSale.setCompanyID(agentCode);
            sPlantEndProductSale.setEndBillID(String.valueOf(System.currentTimeMillis()));
            sPlantEndProductSale.setProductID(productColorMap.get(vivoCustomerSaleImeiDto.getProductId()));
            sPlantEndProductSale.setSaleCount(1);
            sPlantEndProductSale.setImei(vivoCustomerSaleImeiDto.getImei());
            sPlantEndProductSale.setBillDate(vivoCustomerSaleImeiDto.getSaleTime());
            sPlantEndProductSale.setDealerID(StringUtils.getFormatId(vivoCustomerSaleImeiDto.getShopId(),agentCode+"C","00000"));
            sPlantEndProductSale.setCreatedTime(LocalDateTimeUtils.format(LocalDateTime.now()));
            sPlantEndProductSale.setAgentCode(agentCode);
            sPlantEndProductSaleList.add(sPlantEndProductSale);
        }
        sPlantEndProductSaleRepository.deleteByBillDate(dateStart,dateEnd);
        sPlantEndProductSaleRepository.batchSave(sPlantEndProductSaleList);
        logger.info("核销记录数据同步结束:"+LocalDateTime.now());
    }

    @Transactional
    public void pushSStoreData(List<SStores> sStoresList){
        logger.info("一代仓库数据同步开始:"+LocalDateTime.now());
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            sStoresList = Lists.newArrayList();
            String mainCode = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0];
            SStores sStores = new SStores();
            sStores.setStoreID(mainCode + "K0000");
            sStores.setStoreName(mainCode + "大库");
            sStores.setAgentCode(mainCode);
            sStoresList.add(sStores);
        }
        sStoresRepository.deleteAll();
        sStoresRepository.batchSave(sStoresList);
        logger.info("一代仓库数据同步结束:"+LocalDateTime.now());
    }


    @LocalDataSource
    public Map<String,String> getProductColorMap(){
        List<VivoPlantProducts> vivoPlantProductList= vivoPlantProductsRepository.findAllByProductId();
        Map<String, String> productColorMap = Maps.newHashMap();

        for (VivoPlantProducts vivoPlantProduct : vivoPlantProductList){
            String colorId = vivoPlantProduct.getColorId();
            String productId = vivoPlantProduct.getProductId();
            String lxProductId = vivoPlantProduct.getLxProductId();
            if (StringUtils.isNotBlank(colorId)){
                if (StringUtils.isNotBlank(productId)){
                    productColorMap.put(productId,colorId);
                }
                if (StringUtils.isNotBlank(lxProductId)){
                    productColorMap.put(lxProductId,colorId);
                }
            }
        }
        return productColorMap;
    }

    private String getZoneId(String mainCode,String id){
        return StringUtils.getFormatId(id,mainCode,"0000");
    }

    public VivoPushDto getPushFactoryDate(String date,String companyName) {
        String dateStart = date;
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<String> agentCodeList = Lists.newArrayList();
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            agentCodeList.add(CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0]);
        }else if (CompanyNameEnum.IDVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            agentCodeList = officeClient.findDistinctAgentCode(companyName);
        }else {
            return null;
        }
        logger.info("数据准备开始:"+LocalDateTime.now());
        VivoPushDto vivoPushDto = new VivoPushDto();
        vivoPushDto.setsZonesList(sZonesRepository.findByAgentCodeIn(agentCodeList));
        vivoPushDto.setsCustomersList(sCustomersRepository.findByAgentCodeIn(agentCodeList));
        vivoPushDto.setsPlantEndProductSaleList(sPlantEndProductSaleRepository.findByDateAndAgentCodeIn(dateStart,dateEnd,agentCodeList));
        vivoPushDto.setsPlantStockDealerList(sPlantStockDealerRepository.findByDateAndAgentCodeIn(dateStart,dateEnd,agentCodeList));
        vivoPushDto.setsPlantStockStoresList(sPlantStockStoresRepository.findByDateAndAgentCodeIn(dateStart,dateEnd,agentCodeList));
        vivoPushDto.setsPlantStockSupplyList(sPlantStockSupplyRepository.findByDateAndAgentCodeIn(dateStart,dateEnd,agentCodeList));
        vivoPushDto.setsProductItem000List(sProductItem000Repository.findByAgentCodeIn(agentCodeList));
        vivoPushDto.setsProductItemStocksList(sProductItemStocksRepository.findByAgentCodeIn(agentCodeList));
        vivoPushDto.setsStoresList(sStoresRepository.findByAgentCodeIn(agentCodeList));
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            vivoPushDto.setsProductItemLendList(sProductItemLendRepository.findByDate(dateStart,dateEnd));
        }
        logger.info("数据准备完成:"+LocalDateTime.now());
        return vivoPushDto;
    }

    @FactoryDataSource
    @Transactional
    public void pushFactoryData(VivoPushDto vivoPushDto,String date,String companyName){
        logger.info("开始上抛数据:"+LocalDateTime.now());
        List<String> agentCodeList = Lists.newArrayList();
        if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            agentCodeList.add(CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).getValue().split(CharConstant.COMMA)[0]);
        }else if (CompanyNameEnum.IDVIVO.name().equals(DbContextHolder.get().getCompanyName())){
            agentCodeList = officeClient.findDistinctAgentCode(companyName);
        }else {
            return;
        }
        String dateSart = date;
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        for (String agentCode : agentCodeList){

            List<SZones> sZonesList = Lists.newArrayList();
            List<SPlantEndProductSale> sPlantEndProductSaleList = Lists.newArrayList();
            List<SPlantStockDealer> sPlantStockDealerList = Lists.newArrayList();
            List<SPlantStockStores> sPlantStockStoresList = Lists.newArrayList();
            List<SPlantStockSupply> sPlantStockSupplyList = Lists.newArrayList();
            List<SProductItem000> sProductItem000List = Lists.newArrayList();
            List<SProductItemLend> sProductItemLendList = Lists.newArrayList();
            List<SProductItemStocks> sProductItemStocksList = Lists.newArrayList();
            List<SStores> sStoresList = Lists.newArrayList();
            List<SCustomers> sCustomersList = Lists.newArrayList();

            for (SZones sZones : vivoPushDto.getsZonesList()){
                if (agentCode.equals(sZones.getAgentCode())){
                    sZonesList.add(sZones);
                }
            }

            for (SPlantEndProductSale sPlantEndProductSale : vivoPushDto.getsPlantEndProductSaleList()){
                if (agentCode.equals(sPlantEndProductSale.getAgentCode())){
                    sPlantEndProductSaleList.add(sPlantEndProductSale);
                }
            }

            for (SPlantStockDealer sPlantStockDealer : vivoPushDto.getsPlantStockDealerList()){
                if (agentCode.equals(sPlantStockDealer.getAgentCode())){
                    sPlantStockDealerList.add(sPlantStockDealer);
                }
            }

            for (SPlantStockStores sPlantStockStores : vivoPushDto.getsPlantStockStoresList()){
                if (agentCode.equals(sPlantStockStores.getAgentCode())){
                    sPlantStockStoresList.add(sPlantStockStores);
                }
            }

            for (SPlantStockSupply sPlantStockSupply : vivoPushDto.getsPlantStockSupplyList()){
                if (agentCode.equals(sPlantStockSupply.getAgentCode())){
                    sPlantStockSupplyList.add(sPlantStockSupply);
                }
            }

            for (SProductItem000 sProductItem000 : vivoPushDto.getsProductItem000List()){
                if (agentCode.equals(sProductItem000.getAgentCode())){
                    sProductItem000List.add(sProductItem000);
                }
            }

            for (SProductItemStocks sProductItemStocks : vivoPushDto.getsProductItemStocksList()){
                if (agentCode.equals(sProductItemStocks.getAgentCode())){
                    sProductItemStocksList.add(sProductItemStocks);
                }
            }

            for (SStores sStore : vivoPushDto.getsStoresList()){
                if (agentCode.equals(sStore.getAgentCode())){
                    sStoresList.add(sStore);
                }
            }

            for (SCustomers sCustomer : vivoPushDto.getsCustomersList()){
                if (agentCode.equals(sCustomer.getAgentCode())){
                    sCustomersList.add(sCustomer);
                }
            }

            if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
                for (SProductItemLend sProductItemLend : vivoPushDto.getsProductItemLendList()){
                    sProductItemLendList.add(sProductItemLend);
                }
            }

            sZonesRepository.deleteByAgentCode(agentCode);
            sZonesRepository.batchSaveToFactroy(agentCode,sZonesList);

            sCustomersRepository.deleteByAgentCode(agentCode);
            sCustomersRepository.batchSaveToFactory(agentCode,sCustomersList);

            sPlantEndProductSaleRepository.deleteByBillDateAndAgentCode(dateSart,dateEnd,agentCode);
            sPlantEndProductSaleRepository.batchSaveToFactory(agentCode,sPlantEndProductSaleList);

            sPlantStockDealerRepository.deleteByAccountDateAndAgentCode(dateSart,dateEnd,agentCode);
            sPlantStockDealerRepository.batchSaveToFactory(agentCode,sPlantStockDealerList);

            sPlantStockStoresRepository.deleteByAccountDateAndAgentCode(dateSart,dateEnd,agentCode);
            sPlantStockStoresRepository.batchSaveToFactory(agentCode,sPlantStockStoresList);

            sPlantStockSupplyRepository.deleteByAccountDateAndAgentCode(dateSart,dateEnd,agentCode);
            sPlantStockSupplyRepository.batchSaveToFactory(agentCode,sPlantStockSupplyList);

            sProductItem000Repository.deleteByAgentCode(agentCode);
            sProductItem000Repository.batchSaveToFactory(agentCode,sProductItem000List);

            sProductItemStocksRepository.deleteByAgentCode(agentCode);
            sProductItemStocksRepository.batchSaveToFactory(agentCode,sProductItemStocksList);

            sStoresRepository.deleteByAgentCode(agentCode);
            sStoresRepository.batchSaveToFactory(agentCode,sStoresList);

            if (CompanyNameEnum.JXVIVO.name().equals(DbContextHolder.get().getCompanyName())){
                sProductItemLendRepository.deletePushFactoryDataByUpdateTime(dateSart,dateEnd);
                sProductItemLendRepository.batchSavePushFactoryData(sProductItemLendList);
            }
        }
        logger.info("上抛数据完成:"+LocalDateTime.now());
    }

}
