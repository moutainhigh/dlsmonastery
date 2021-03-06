package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.ArReceiveBillDto;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.service.ArReceiveBillService;
import net.myspring.cloud.modules.input.web.form.ArReceiveBillForm;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeSyn;
import net.myspring.cloud.modules.sys.dto.KingdeeSynReturnDto;
import net.myspring.cloud.modules.sys.service.AccountKingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeSynService;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.mapper.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 收款单
 * Created by lihx on 2017/6/24.
 */
@RestController
@RequestMapping(value = "input/arReceiveBill")
public class ArReceiveBillController {
    private static Logger logger = LoggerFactory.getLogger(ArReceiveBillController.class);
    @Autowired
    private ArReceiveBillService arReceiveBillService;
    @Autowired
    private KingdeeBookService kingdeeBookService;
    @Autowired
    private AccountKingdeeBookService accountKingdeeBookService;
    @Autowired
    private KingdeeSynService kingdeeSynService;

    @RequestMapping(value = "form")
    public ArReceiveBillForm form () {
        return arReceiveBillService.getForm();
    }

    @RequestMapping(value = "save")
    public RestResponse save(ArReceiveBillForm arReceiveBillForm) {
        RestResponse restResponse;
        StringBuilder message = new StringBuilder();
        try {
            AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
            if (accountKingdeeBook != null) {
                KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
                List<KingdeeSynDto> kingdeeSynDtoList = arReceiveBillService.save(arReceiveBillForm, kingdeeBook, accountKingdeeBook);
                kingdeeSynService.save(BeanUtil.map(kingdeeSynDtoList, KingdeeSyn.class));
                for (KingdeeSynDto kingdeeSynDto : kingdeeSynDtoList) {
                    if (kingdeeSynDto.getSuccess()) {
                        message.append(kingdeeSynDto.getBillNo()+",");
                    }
                }
                restResponse = new RestResponse("收款单成功：" + message, null, true);
            } else {
                restResponse = new RestResponse("您没有金蝶账号，不能开单", null, false);
            }
            return restResponse;
        }catch (Exception e){
            return new RestResponse(e.getMessage(), ResponseCodeEnum.invalid.name(), false);
        }
    }

    @RequestMapping(value = "saveForWS",method = RequestMethod.POST)
    public List<KingdeeSynReturnDto> saveForShopDeposit(@RequestBody List<ArReceiveBillDto> arReceiveBillDtoList) {
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
        List<KingdeeSyn> kingdeeSynList;
        if(accountKingdeeBook != null) {
            KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
            List<KingdeeSynDto> kingdeeSynDtoList = arReceiveBillService.saveForWS(arReceiveBillDtoList, kingdeeBook, accountKingdeeBook);
            kingdeeSynList = kingdeeSynService.save(BeanUtil.map(kingdeeSynDtoList, KingdeeSyn.class));
        }else {
            logger.info("您没有金蝶账号，不能开单：用户ID为" + RequestUtils.getAccountId() );
            throw new ServiceException("您没有金蝶账号，不能开单：用户ID为:[" + RequestUtils.getAccountId() + "]" );
        }
        return BeanUtil.map(kingdeeSynList,KingdeeSynReturnDto.class);
    }
}
