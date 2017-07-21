package net.myspring.tool.modules.oppo.web.controller;

import com.google.common.collect.Maps;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.tool.common.client.CompanyConfigClient;
import net.myspring.tool.common.dataSource.DbContextHolder;
import net.myspring.tool.modules.oppo.domain.*;
import net.myspring.tool.modules.oppo.dto.OppoPlantSendImeiPpselDto;
import net.myspring.tool.modules.oppo.dto.OppoResponseMessage;
import net.myspring.tool.modules.oppo.service.OppoPullService;
import net.myspring.tool.modules.oppo.service.OppoPushSerivce;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.MD5Utils;
import net.myspring.util.time.LocalDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "factory/oppo")
public class OppoPullController {
    @Autowired
    private OppoPullService oppoService;
    @Autowired
    private CompanyConfigClient companyConfigClient;

    @RequestMapping(value = "pullFactoryData")
    public String pullFactoryData(String date) {
        String agentCode=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_CODES.name()).replace("\"","");
        String[] agentCodes=agentCode.split(CharConstant.COMMA);
        String passWord=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.FACTORY_AGENT_PASSWORDS.name()).replace("\"","");
        String[] passWords=passWord.split(CharConstant.COMMA);
        //同步颜色编码
        String companyName=companyConfigClient.getValueByCode(CompanyConfigCodeEnum.COMPANY_NAME.name()).replace("\"","");
        DbContextHolder.get().setCompanyName(companyName);
        List<OppoPlantProductSel> oppoPlantProductSels=oppoService.getOppoPlantProductSels(agentCodes[0],passWords[0]);
        oppoService.pullPlantProductSels(oppoPlantProductSels);
        //同步物料编码
        List<OppoPlantAgentProductSel> oppoPlantAgentProductSels=oppoService.getOppoPlantAgentProductSels(agentCodes[0],passWords[0]);
        oppoService.pullPlantAgentProductSels(oppoPlantAgentProductSels);
        //同步发货串码
        Map<String,List<OppoPlantSendImeiPpsel>> oppoPlantSendImeiPpselMap= Maps.newHashMap();
        for (int i = 0; i < agentCodes.length; i++) {
            oppoPlantSendImeiPpselMap.put(agentCodes[i],oppoService.getOppoPlantSendImeiPpsels(agentCodes[i],passWords[i],date)) ;
        }
        oppoService.pullPlantSendImeiPpsels(oppoPlantSendImeiPpselMap);
        //同步电子保卡
        List<OppoPlantProductItemelectronSel> oppoPlantProductItemelectronSels=oppoService.getOppoPlantProductItemelectronSels(agentCodes[0],passWords[0],date);
        oppoService.pullPlantProductItemelectronSels(oppoPlantProductItemelectronSels);
        return "OPPO同步成功";
    }


    @RequestMapping(value = "synIme")
    public List<OppoPlantSendImeiPpselDto> synIme(String date, String agentCode) {
        List<OppoPlantSendImeiPpselDto> oppoPlantSendImeiPpselDtos = oppoService.synIme(date,agentCode);
        return oppoPlantSendImeiPpselDtos;
    }

    @RequestMapping(value = "synProductItemelectronSel")
    public List<OppoPlantProductItemelectronSel> synProductItemelectronSel(String date,String agentCode) {
        List<OppoPlantProductItemelectronSel> oppoPlantProductItemelectronSels = oppoService.synProductItemelectronSel(date,agentCode);
        return oppoPlantProductItemelectronSels;
    }

}
