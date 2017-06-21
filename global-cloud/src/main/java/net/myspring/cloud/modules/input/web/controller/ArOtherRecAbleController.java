package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.service.ArOtherRecAbleService;
import net.myspring.cloud.modules.input.web.form.ArOtherRecAbleForm;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.service.AccountKingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeBookService;
import net.myspring.common.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 其他应收单
 * Created by lihx on 2017/6/20.
 */
@RestController
@RequestMapping(value = "input/arOtherRecAble")
public class ArOtherRecAbleController {
    @Autowired
    private ArOtherRecAbleService arOtherRecAbleService;
    @Autowired
    private KingdeeBookService kingdeeBookService;
    @Autowired
    private AccountKingdeeBookService accountKingdeeBookService;

    @RequestMapping(value = "form")
    public ArOtherRecAbleForm form () {
        return arOtherRecAbleService.getForm();
    }

    @RequestMapping(value = "save")
    public RestResponse save(ArOtherRecAbleForm arOtherRecAbleForm) {
        RestResponse restResponse = new RestResponse("其他应收单失败", null, false);
        KingdeeBook kingdeeBook = kingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountId(RequestUtils.getAccountId());
        List<KingdeeSynDto> kingdeeSynDtoList = arOtherRecAbleService.save(arOtherRecAbleForm,kingdeeBook,accountKingdeeBook);
        for (KingdeeSynDto kingdeeSynDto : kingdeeSynDtoList) {
            if (kingdeeSynDto.getSuccess()) {
                restResponse = new RestResponse("其他应收单成功：" + kingdeeSynDto.getBillNo(), null, true);
            } else {
                System.err.println(kingdeeSynDto.getResult());
                restResponse = new RestResponse("其他应收单失败：" + kingdeeSynDto.getResult(), null, false);
            }
        }
        return restResponse;
    }

}