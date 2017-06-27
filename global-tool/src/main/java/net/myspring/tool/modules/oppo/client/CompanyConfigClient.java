package net.myspring.tool.modules.oppo.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("summer-basic")
public interface CompanyConfigClient {

    @RequestMapping(method = RequestMethod.GET, value = "/sys/companyConfig/getValueByCode")
    String getValueByCode(@RequestParam(value = "code") String code);
}