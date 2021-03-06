package net.myspring.future.modules.basic.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.exception.ServiceException;
import net.myspring.future.common.enums.BillTypeEnum;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.common.enums.CompanyNameEnum;
import net.myspring.future.common.enums.NetTypeEnum;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.dto.ProductAdApplyDto;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.basic.repository.ProductRepository;
import net.myspring.future.modules.basic.web.form.ProductBatchForm;
import net.myspring.util.text.IdUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.domain.Product;
import net.myspring.future.modules.basic.dto.ProductDto;
import net.myspring.future.modules.basic.web.form.ProductForm;
import net.myspring.future.modules.basic.web.query.ProductQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CloudClient cloudClient;
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    

    public Page<ProductDto> findPage(Pageable pageable, ProductQuery productQuery) {
        Page<ProductDto> page = productRepository.findPage(pageable, productQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public List<ProductDto> findFilter(ProductQuery productQuery){
        List<ProductDto> productDtoList = productRepository.findFilter(productQuery);
        return productDtoList;
    }


    public List<ProductDto> findByIds(List<String> ids){
        List<Product> productList=productRepository.findByEnabledIsTrueAndIdIn(ids);
        List<ProductDto> productDtoList= BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public Product findByName(String name){
        return productRepository.findByName(name);
    }
    public List<ProductDto> findByNameOrCodeAndOutGroupName(String nameOrCode,String outGroupName){
        return productRepository.findByNameOrCodeAndOutGroupName(nameOrCode,outGroupName);
    }

    public List<ProductDto> findByNameLikeHasIme(String name){
        List<Product> productList=productRepository.findByNameLikeHasIme(name);
        List<ProductDto> productDtoList= BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public List<ProductDto> findByCodeLikeHasIme(String code){
        List<Product> productList=productRepository.findByCodeLikeHasIme(code);
        List<ProductDto> productDtoList= BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public List<ProductDto> findByNameLike(String name){
        List<Product> productList=productRepository.findByNameLike(name);
        List<ProductDto> productDtoList= BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public List<ProductDto> findByCodeLike(String code){
        List<Product> productList=productRepository.findByCodeLike(code);
        List<ProductDto> productDtoList= BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public ProductDto findOne(String id) {
        ProductDto productDto = new ProductDto();
        if(StringUtils.isNotBlank(id)){
            Product product=productRepository.findOne(id);
            productDto = BeanUtil.map(product,ProductDto.class);
            cacheUtils.initCacheInput(productDto);
        }
        return productDto;
    }

    public List<ProductDto> findHasImeProduct(){
        List<Product> productList=productRepository.findHasImeProduct();
        List<ProductDto> productDtoList= BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public List<ProductDto> findByOutGroupIds(List<String> outGroupIds){
        List<ProductDto> productDtoList= BeanUtil.map(productRepository.findByOutGroupIdIn(outGroupIds),ProductDto.class);
        return productDtoList;
    }

    public  List<ProductAdApplyDto> findAdProductAndAllowOrder(String billType,String depotId){
        List<String> outGroupIds =Lists.newArrayList();
        if(BillTypeEnum.POP.name().equals(billType)){
            String value = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.PRODUCT_POP_GROUP_IDS.name()).getValue();
            outGroupIds = IdUtils.getIdList(value);
        }else if (BillTypeEnum.配件赠品.name().equals(billType)){
            String value = CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.PRODUCT_GOODS_POP_GROUP_IDS.name()).getValue();
            outGroupIds = IdUtils.getIdList(value);
        }
        List<Product> products = productRepository.findByOutGroupIdInAndAllowOrderIsTrue(outGroupIds);
        //电教系统POP货品选择
        if(StringUtils.isNotBlank(depotId)){
            Depot depot = depotRepository.findOne(depotId);
            if(depot != null&&BillTypeEnum.POP.name().equalsIgnoreCase(billType)&&
                    RequestUtils.getCompanyName().equalsIgnoreCase(CompanyNameEnum.JXDJ.name())){
                Iterator<Product> iterator = products.iterator();
                while (iterator.hasNext()){
                    Product product = iterator.next();
                    if(depot.getCode().startsWith("IM0")){
                        if(!product.getOutGroupName().startsWith("immo")){
                            iterator.remove();
                        }

                    }else if(depot.getCode().startsWith("DJ")){
                        if(!product.getOutGroupName().startsWith("电玩")){
                            iterator.remove();
                        }

                    }
                }
            }
        }

        List<ProductAdApplyDto> productAdApplyDtos  = BeanUtil.map(products,ProductAdApplyDto.class);
        return productAdApplyDtos;
    }

    @Transactional
    public void save(ProductForm productForm) {
        Product product;
        if(productForm.isCreate()){
            product = BeanUtil.map(productForm,Product.class);
            productRepository.save(product);
        }else{
            product = productRepository.findOne(productForm.getId());
            ReflectionUtil.copyProperties(productForm,product);
            productRepository.save(product);
        }
    }

    @Transactional
    public void batchSave(ProductBatchForm productBatchForm){
        String json = HtmlUtils.htmlUnescape(productBatchForm.getProductList());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        List<Product> productList = productRepository.findAllEnabled();
        Map<String,Product> productMap = CollectionUtil.extractToMap(productList,"id");
        for(List<Object> rows:data){
            Product product = productMap.get(StringUtils.toString(rows.get(0)).trim());
            for (int i = 3; i < rows.size(); i++) {
                String value = StringUtils.toString(rows.get(i)).trim();
                switch (i) {
                    case 3:
                        /* product.setVisible(Boolean.TRUE.toString().equalsIgnoreCase(value));*/
                        product.setVisible("是".equals(value));
                        break;
                    case 4:
                       /* product.setAllowOrder(Boolean.TRUE.toString().equalsIgnoreCase(value));*/
                        product.setAllowOrder("是".equals(value));
                        break;
                    case 5:
                        if (StringUtils.isBlank(value)) {
                            product.setPrice2(BigDecimal.ZERO);
                        } else {
                            product.setPrice2(new BigDecimal(value));
                        }
                    case 6:
                        product.setExpiryDateRemarks(value);
                        break;
                    case 7:
                        if (StringUtils.isBlank(value)) {
                            product.setVolume(null);
                        } else {
                            product.setVolume(new BigDecimal(value));
                        }
                        break;
                    case 8:
                        product.setRemarks(value);
                        break;
                    default:
                        break;
                }
            }
            productRepository.save(product);
        }
        //productRepository.save(productList);
    }

    public ProductQuery getQuery(ProductQuery productQuery){
        productQuery.getExtra().put("netTypeList",NetTypeEnum.getList());
        productQuery.getExtra().put("outGroupNameList",productRepository.findByOutName());
        return productQuery;
    }

    public ProductForm getForm(ProductForm productForm){
        productForm.getExtra().put("netTypeList",NetTypeEnum.getList());
        return productForm;
    }

    @Transactional
    public String syn() {
        String newProductName = "";
        List<BdMaterial> bdMaterials = cloudClient.getAllProduct();
        List<Product> products = productRepository.findAllEnabled();
        Map<String,Product> productMapByOutId = CollectionUtil.extractToMap(products,"outId");
        Map<String,Product> productMapByName = CollectionUtil.extractToMap(products,"name");
        List<String> goodsOrderIds = IdUtils.getIdList(CompanyConfigUtil.findByCode(redisTemplate,CompanyConfigCodeEnum.PRODUCT_GOODS_GROUP_IDS.name()).getValue());
        //List<Product> synProduct = Lists.newArrayList();
        if(CollectionUtil.isNotEmpty(bdMaterials)){
            for(BdMaterial bdMaterial:bdMaterials){
                Product product = productMapByOutId.get(bdMaterial.getFMasterId());
                if(product == null){
                    product = productMapByName.get(bdMaterial.getFName());
                    if(product == null){
                        product = new Product();
                        product.setCreatedBy(RequestUtils.getAccountId());
                        product.setShouldGet(BigDecimal.ZERO);
                        product.setVolume(BigDecimal.ZERO);
                        product.setAllowOrder(false);
                        product.setAllowBill(false);
                        product.setVisible(true);
                        if(goodsOrderIds.contains(bdMaterial.getFMaterialGroup())){
                            product.setHasIme(true);
                        }else{
                            product.setHasIme(false);
                        }
                        newProductName += bdMaterial.getFName()+CharConstant.SPACE;
                    }else{
                        product.setOldName(product.getName());
                        product.setOldOutId(product.getOutId());
                        product.setLastModifiedBy(RequestUtils.getAccountId());
                    }
                }
                setProductNetType(product,bdMaterial);
                product.setOutDate(bdMaterial.getFModifyDate());
                product.setName(bdMaterial.getFName());
                product.setOutId(bdMaterial.getFMasterId());
                product.setOutGroupId(bdMaterial.getFMaterialGroup().toString());
                product.setOutGroupName(bdMaterial.getFMaterialGroupName());
                product.setCode(bdMaterial.getFNumber());
                productRepository.save(product);
            }
            //productRepository.save(synProduct);
        }
        return newProductName;
    }

    @Transactional
    public void delete(String id) {
        if(StringUtils.isNotBlank(id)){
            Product product = productRepository.findOne(id);
            product.setName(product.getName()+"(废弃"+LocalDateTime.now()+")");
            product.setEnabled(false);
            productRepository.save(product);
        }else {
            throw new ServiceException("未选中任何货品");
        }

    }

    public List<ProductDto> findIntersectionOfBothPricesystem(String pricesystemId1, String pricesystemId2) {
        return  productRepository.findIntersectionOfBothPricesystem(pricesystemId1, pricesystemId2);
    }

    public List<String> findNameList() {
        return CollectionUtil.extractToList(productRepository.findByEnabledIsTrue(), "name");
    }

    public String findAdProductCodeAndAllowOrder(){
        List<String> outIds = IdUtils.getIdList(CompanyConfigUtil.findByCode(redisTemplate, CompanyConfigCodeEnum.PRODUCT_POP_GROUP_IDS.name()).getValue()
                    +CharConstant.COMMA+CompanyConfigUtil.findByCode(redisTemplate, CompanyConfigCodeEnum.PRODUCT_GOODS_POP_GROUP_IDS.name()).getValue());
        List<Product> productList = productRepository.findByOutGroupIdInAndAllowOrderIsTrue(outIds);
        List<String> productCodes = CollectionUtil.extractToList(productList,"code");
        if(productCodes!=null&&productCodes.size()>0){
            return productCodes.toString();
        }else{
            return "";
        }
    }

    private void setProductNetType(Product product,BdMaterial bdMaterial) {
        if (CompanyNameEnum.JXOPPO.name().equalsIgnoreCase(RequestUtils.getCompanyName()) || CompanyNameEnum.JXVIVO.name().equalsIgnoreCase(RequestUtils.getCompanyName())) {
            if (bdMaterial.getFMaterialGroupName().equalsIgnoreCase("商品类")) {
                if (bdMaterial.getFName().trim().contains(NetTypeEnum.移动.name())) {
                    product.setNetType(NetTypeEnum.移动.name());
                } else {
                    product.setNetType(NetTypeEnum.联信.name());
                }
            } else {
                product.setNetType(NetTypeEnum.全网通.name());
            }
        }else{
            product.setNetType(NetTypeEnum.全网通.name());
        }
    }
}
