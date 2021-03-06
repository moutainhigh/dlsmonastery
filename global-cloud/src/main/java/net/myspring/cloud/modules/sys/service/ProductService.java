package net.myspring.cloud.modules.sys.service;

import net.myspring.cloud.common.dataSource.annotation.LocalDataSource;
import net.myspring.cloud.common.enums.KingdeeNameEnum;
import net.myspring.common.utils.HandsontableUtils;
import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.domain.Product;
import net.myspring.cloud.modules.sys.dto.ProductDto;
import net.myspring.cloud.modules.sys.repository.ProductRepository;
import net.myspring.cloud.modules.sys.web.form.ProductForm;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liuj on 2017/4/5.
 */
@Service
@LocalDataSource
@Transactional(readOnly = true)
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public LocalDateTime findMaxOutDateByCompanyName(){
        return productRepository.findMaxOutDateByCompanyName(RequestUtils.getCompanyName());
    }

    public ProductForm getForm(ProductForm productForm){
        List<String> productList = productRepository.findNameByCompanyName(RequestUtils.getCompanyName());
        productForm.setProductNameList(productList);
        return productForm;
    }

    public List<ProductDto> findByCompanyName(){
        List<Product> productList = productRepository.findByCompanyName(RequestUtils.getCompanyName());
        List<ProductDto> productDtoList = BeanUtil.map(productList,ProductDto.class);
        return productDtoList;
    }

    public ProductDto findByNameAndCompanyName(String nameHtml){
        if (StringUtils.isNotBlank(nameHtml)) {
            String name = HtmlUtils.htmlUnescape(nameHtml);
            Product product = productRepository.findByNameAndCompanyName(RequestUtils.getCompanyName(), name);
            ProductDto productDto = BeanUtil.map(product, ProductDto.class);
            return productDto;
        }
        return null;
    }

    public String findReturnOutIdByCompanyName(){
        return productRepository.findReturnOutIdByCompanyName(RequestUtils.getCompanyName());
    }

    public ProductDto findByOutIdAndCompanyName(String outId){
        Product product =  productRepository.findByOutIdAndCompanyName(outId,RequestUtils.getCompanyName());
        ProductDto productDto = BeanUtil.map(product, ProductDto.class);
        return productDto;
    }

    public ProductDto findByCodeAndCompanyName(String code){
        if (StringUtils.isNotBlank(code)){
            Product product = productRepository.findByCodeAndCompanyName(RequestUtils.getCompanyName(),code);
            ProductDto productDto = BeanUtil.map(product,ProductDto.class);
            return productDto;
        }
        return null;
    }

    @Transactional
    public void save(ProductForm productForm) {
        String json = HtmlUtils.htmlUnescape(productForm.getJson());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        List<Product> productList = productRepository.findByCompanyName(RequestUtils.getCompanyName());
        Map<String,Product> productMap = productList.stream().collect(Collectors.toMap(Product::getCode, Product->Product));
        for (List<Object> row : data) {
            String code = HandsontableUtils.getValue(row,1);
            String priceStr = HandsontableUtils.getValue(row,2);
            BigDecimal price = StringUtils.isEmpty(priceStr) ? BigDecimal.ZERO : new BigDecimal(priceStr);
            if(productMap.get(code) != null){
                Product product = productMap.get(code);
                product.setPrice1(price);
                productRepository.save(product);
            }
        }
    }

    @Transactional
    public void syn(List<BdMaterial> bdMaterialList,KingdeeBook kingdeeBook){
        String returnOutId = "";
        String companyName = RequestUtils.getCompanyName();
        if(!KingdeeNameEnum.JXDJ.name().equals(kingdeeBook.getName())){
            returnOutId = productRepository.findReturnOutIdByCompanyName(companyName);
        }
        Map<String,Product> productMap = productRepository.findByCompanyName(companyName).stream().collect(Collectors.toMap(Product::getOutId,Product->Product));
        for (BdMaterial bdmaterial : bdMaterialList) {
            Product product = productMap.get(bdmaterial.getFMasterId());
            if (product == null) {
                product = new Product();
                product.setCompanyName(companyName);
                product.setName(bdmaterial.getFName());
                product.setCode(bdmaterial.getFNumber());
                product.setOutId(bdmaterial.getFMasterId());
                product.setOutDate(bdmaterial.getFModifyDate());
                product.setReturnOutId(returnOutId);
                product.setKingdeeBookId(kingdeeBook.getId());
                productRepository.save(product);
            } else {
                product.setCode(bdmaterial.getFNumber());
                product.setOutDate(bdmaterial.getFModifyDate());
                product.setName(bdmaterial.getFName());
                product.setKingdeeBookId(kingdeeBook.getId());
                productRepository.save(product);
            }
        }    
    }

}
