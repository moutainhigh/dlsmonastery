package net.myspring.future.modules.basic.manager;

import com.google.common.collect.Lists;
import net.myspring.cloud.common.enums.ExtendTypeEnum;
import net.myspring.cloud.modules.input.dto.SalOutStockDto;
import net.myspring.cloud.modules.input.dto.SalOutStockFEntityDto;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.domain.DepotStore;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.dto.ClientDto;
import net.myspring.future.modules.basic.repository.ClientRepository;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.basic.repository.DepotStoreRepository;
import net.myspring.future.modules.basic.repository.ProductRepository;
import net.myspring.future.modules.crm.domain.GoodsOrderDetail;
import net.myspring.future.modules.crm.repository.GoodsOrderDetailRepository;
import net.myspring.future.modules.crm.web.form.GoodsOrderForm;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 销售出库单
 * Created by lihx on 2017/6/26.
 */
@Component
public class SalOutStockManager {
    @Autowired
    private GoodsOrderDetailRepository goodsOrderDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private DepotStoreRepository depotStoreRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CloudClient cloudClient;

    public List<KingdeeSynReturnDto> synForGoodsOrder(GoodsOrderForm goodsOrderForm){
        if (StringUtils.isNotBlank(goodsOrderForm.getId())){
            List<GoodsOrderDetail> goodsOrderDetailList = goodsOrderDetailRepository.findByGoodsOrderId(goodsOrderForm.getId());
            DepotStore depotStore = depotStoreRepository.findByEnabledIsTrueAndDepotId(goodsOrderForm.getStoreId());
            ClientDto clientDto = clientRepository.findByDepotId(goodsOrderForm.getShopId());
            List<String> productIdList = goodsOrderDetailList.stream().map(GoodsOrderDetail::getProductId).collect(Collectors.toList());
            Map<String,String> productIdToOutCodeMap = productRepository.findByEnabledIsTrueAndIdIn(productIdList).stream().collect(Collectors.toMap(Product::getId,Product::getCode));
            List<SalOutStockDto> salOutStockDtoList = Lists.newArrayList();
            SalOutStockDto salOutStockDto = new SalOutStockDto();
            salOutStockDto.setExtendId(goodsOrderForm.getId());
            salOutStockDto.setExtendType(ExtendTypeEnum.货品订货.name());
            salOutStockDto.setDate(LocalDate.now());
            salOutStockDto.setCustomerNumber(clientDto.getOutCode());
            salOutStockDto.setNote(goodsOrderForm.getRemarks());
            List<SalOutStockFEntityDto> entityDtoList = Lists.newArrayList();
            for (GoodsOrderDetail detail : goodsOrderDetailList) {
                if (detail.getBillQty() != null && detail.getBillQty() > 0) {
                    SalOutStockFEntityDto entityDto = new SalOutStockFEntityDto();
                    entityDto.setStockNumber(depotStore.getOutCode());
                    entityDto.setMaterialNumber(productIdToOutCodeMap.get(detail.getProductId()));
                    entityDto.setQty(detail.getBillQty());
                    entityDto.setPrice(detail.getPrice());
                    entityDto.setEntryNote(goodsOrderForm.getRemarks());
                    entityDtoList.add(entityDto);
                    salOutStockDto.setSalOutStockFEntityDtoList(entityDtoList);
                    salOutStockDtoList.add(salOutStockDto);
                }
            }
            return cloudClient.synSalOutStock(salOutStockDtoList);
        }
        return null;
    }
}