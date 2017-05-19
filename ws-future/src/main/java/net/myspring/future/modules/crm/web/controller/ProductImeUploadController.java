package net.myspring.future.modules.crm.web.controller;


import net.myspring.future.modules.crm.domain.ProductImeUpload;
import net.myspring.future.modules.crm.dto.ProductImeUploadDto;
import net.myspring.future.modules.crm.service.ProductImeUploadService;
import net.myspring.future.modules.crm.web.query.ProductImeUploadQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "crm/productImeUpload")
public class ProductImeUploadController {

    @Autowired
    private ProductImeUploadService productImeUploadService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<ProductImeUploadDto> list(Pageable pageable, ProductImeUploadQuery productImeUploadQuery){

        Page<ProductImeUploadDto> page = productImeUploadService.findPage(pageable, productImeUploadQuery);
        return page;
    }


    @RequestMapping(value = "save")
    public String save(ProductImeUpload productImeUpload, BindingResult bindingResult) {
        return null;
    }

    @RequestMapping(value = "delete")
    public String delete(ProductImeUpload productImeUpload, BindingResult bindingResult){
        return null;
    }

    @RequestMapping(value = "audit")
    public String audit(@RequestParam(value = "ids[]") String[] ids,Boolean pass){
        return null;
    }

    private List<String> getActionList(ProductImeUpload productImeUpload) {
        return null;
    }

}
