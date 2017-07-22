package net.myspring.tool.modules.future.service;

import net.myspring.tool.common.dataSource.annotation.FutureDataSource;
import net.myspring.tool.modules.future.repository.FutureProductImeSaleRepository;
import net.myspring.tool.modules.oppo.domain.OppoCustomerSale;
import net.myspring.tool.modules.oppo.domain.OppoCustomerSaleCount;
import net.myspring.tool.modules.oppo.domain.OppoCustomerSaleImei;
import net.myspring.tool.modules.vivo.dto.VivoCustomerSaleImeiDto;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@FutureDataSource
public class FutureProductImeSaleService {
    @Autowired
    private FutureProductImeSaleRepository futureProductImeSaleRepository;

    public List<VivoCustomerSaleImeiDto> getProductImeSaleData(String date){
        if (StringUtils.isBlank(date)){
            date = LocalDateUtils.format(LocalDate.now());
        }
        String dateStart = date;
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        return futureProductImeSaleRepository.findProductSaleImei(dateStart,dateEnd);
    }

    public List<OppoCustomerSale> getFutureOppoCustomerSale(String date){
        if(StringUtils.isEmpty(date)){
            date=LocalDateUtils.format(LocalDate.now());
        }
        String dateStart= date;
        String dateEnd=LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<OppoCustomerSale>  oppoCustomerSales=futureProductImeSaleRepository.findCustomerSales(dateStart,dateEnd);
        return oppoCustomerSales;
    }

    public List<OppoCustomerSaleImei> getFutureOppoCustomerSaleImeis(String date){
        if(StringUtils.isEmpty(date)){
            date=LocalDateUtils.format(LocalDate.now());
        }
        String dateStart= date;
        String dateEnd=LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<OppoCustomerSaleImei> oppoCustomerSaleImeis=futureProductImeSaleRepository.findCustomerSaleImeis(dateStart,dateEnd);
        return oppoCustomerSaleImeis;
    }



    public List<OppoCustomerSaleCount> getFutureOppoCustomerSaleCounts(String date){
        if(StringUtils.isEmpty(date)){
            date=LocalDateUtils.format(LocalDate.now());
        }
        String dateStart= date;
        String dateEnd=LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<OppoCustomerSaleCount> oppoCustomerSaleCounts=futureProductImeSaleRepository.findCustomerSaleCounts(dateStart,dateEnd);
        return oppoCustomerSaleCounts;
    }

}
