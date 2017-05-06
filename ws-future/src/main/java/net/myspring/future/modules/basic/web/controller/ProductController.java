package net.myspring.future.modules.basic.web.controller;

import com.google.common.collect.Lists;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.common.enums.BoolEnum;
import net.myspring.future.common.enums.NetTypeEnum;
import net.myspring.future.modules.basic.dto.ProductDto;
import net.myspring.future.modules.basic.service.ProductService;
import net.myspring.future.modules.basic.service.ProductTypeService;
import net.myspring.future.modules.basic.web.query.ProductQuery;
import net.myspring.future.modules.basic.web.form.ProductForm;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "basic/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductTypeService productTypeService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<ProductDto> list(Pageable pageable, ProductQuery productQuery){
        Page<ProductDto> page = productService.findPage(pageable,productQuery);
        return page;
    }

    @RequestMapping(value = "filter")
    public List<ProductDto> filter(ProductQuery productQuery){
        List<ProductDto> productList = productService.findFilter(productQuery);
        return productList;
    }

    @RequestMapping(value = "getListProperty")
    public ProductQuery getListProperty(ProductQuery productQuery) {
        productQuery.setNetTypeList(NetTypeEnum.getList());
        productQuery.setOutGroupNameList(productService.findByOutName());
        productQuery.setBoolMap(BoolEnum.getMap());
        productQuery.setProductTypeList(productTypeService.findAll());
        return productQuery;
    }

    @RequestMapping(value = "findForm")
    public ProductForm findOne(ProductForm productForm){
        productForm=productService.findForm(productForm);
        productForm.setNetTypeList(NetTypeEnum.getList());
        return productForm;
    }

    @RequestMapping(value = "findHasImeProduct")
    public List<ProductDto> findHasImeProduct(){
        List<ProductDto> productList= productService.findHasImeProduct();
        return productList;
    }

    @RequestMapping(value = "searchAll")
    public List<ProductDto> searchAll(String name,String code){
        List<ProductDto> productList = Lists.newArrayList();
        if(StringUtils.isNotBlank(name)){
            productList = productService.findByNameLike(name);
        }else if(StringUtils.isNotBlank(code)){
            productList = productService.findByCodeLike(code);
        }
        return productList;
    }

    @RequestMapping(value = "search")
    public List<ProductDto> search(String name,String code){
        List<ProductDto> productList = Lists.newArrayList();
        if(StringUtils.isNotBlank(name)){
            productList = productService.findByNameLikeHasIme(name);
        }else if(StringUtils.isNotBlank(code)){
            productList = productService.findByCodeLikeHasIme(code);
        }
        return productList;
    }

    @RequestMapping(value = "save")
    public RestResponse save(ProductForm productForm) {
        productService.save(productForm);
        return new RestResponse("保存成功", ResponseCodeEnum.saved.name());
    }


    @RequestMapping(value = "syn")
    public RestResponse syn() {
        productService.syn();
        return new RestResponse("同步成功",null);
    }

    @RequestMapping(value="getQuery")
    public ProductQuery getQuery(ProductQuery productQuery){
        productQuery.setNetTypeList(productService.findNetTypeList());
        productQuery.setBoolMap(productService.getMap());
        productQuery.setProductTypeList(productService.findProductTypeList());
        productQuery.setOutGroupNameList(productService.findByOutName());
        return productQuery;
    }

    @RequestMapping(value = "delete")
    public RestResponse delete(ProductDto productDto) {
        productService.delete(productDto);
        return new RestResponse("删除成功", ResponseCodeEnum.removed.name());
    }
}
