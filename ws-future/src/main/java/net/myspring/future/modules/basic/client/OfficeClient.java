package net.myspring.future.modules.basic.client;

import net.myspring.basic.modules.sys.dto.OfficeDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * Created by wangzm on 2017/4/21.
 */
@FeignClient("summer-basic")
public interface OfficeClient {

    @RequestMapping(method = RequestMethod.GET, value = "/sys/office/getTopIdsByFilter")
    List<String> getTopIdsByFilter();

    @RequestMapping(method = RequestMethod.GET,value = "/sys/office/findByOfficeRuleName")
    List<OfficeDto> findByOfficeRuleName(@RequestParam(value = "officeRuleName")String officeRuleName);

    @RequestMapping(method = RequestMethod.GET,value = "/sys/office/getMapByOfficeRuleName")
    Map<String,List<String>> getMapByOfficeRuleName(@RequestParam(value = "officeRuleName")String officeRuleName);

    @RequestMapping(method = RequestMethod.GET, value = "/sys/office/getSameAreaByOfficeId")
    List<String> getSameAreaByOfficeId(@RequestParam(value = "officeId") String officeId);

    @RequestMapping(method = RequestMethod.GET, value = "/sys/office/getChildOfficeIds")
    List<String> getChildOfficeIds(@RequestParam(value = "officeId") String officeId);

    @RequestMapping(method = RequestMethod.GET, value = "/sys/office/geMapByOfficeId")
    Map<String,List<String>> geMapByOfficeId(@RequestParam(value = "officeId") String officeId);

    @RequestMapping(method = RequestMethod.GET,value = "/sys/office/findAll")
    List<OfficeDto> findAll();
}
