package net.myspring.tool.modules.future.service;

import net.myspring.tool.common.dataSource.DbContextHolder;
import net.myspring.tool.common.dataSource.annotation.FutureDataSource;
import net.myspring.tool.common.utils.CacheUtils;
import net.myspring.tool.modules.future.repository.FutureProductImeRepository;
import net.myspring.tool.modules.oppo.domain.OppoCustomerStock;
import net.myspring.tool.modules.vivo.dto.SPlantCustomerStockDetailDto;
import net.myspring.tool.modules.vivo.dto.SPlantCustomerStockDto;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@FutureDataSource
@Transactional(readOnly = true)
public class FutureProductImeService {
    @Autowired
    private FutureProductImeRepository futureProductImeRepository;
    @Autowired
    private CacheUtils cacheUtils;


    public List<SPlantCustomerStockDto> getVivoCustomerStockData(String date){
        LocalDate dateStart = LocalDateUtils.parse(date).minusYears(1);
        LocalDate dateEnd = LocalDateUtils.parse(date).plusDays(1);
        List<SPlantCustomerStockDto> sPlantCustomerStockDtoList = futureProductImeRepository.findVivoCustomerStockData(dateStart,dateEnd);
        cacheUtils.initCacheInput(sPlantCustomerStockDtoList);
        return sPlantCustomerStockDtoList;
    }

    public List<SPlantCustomerStockDto> getIDVivoCustomerStockData(String date){
        LocalDate dateStart = LocalDateUtils.parse(date).minusYears(1);
        LocalDate dateEnd = LocalDateUtils.parse(date).plusDays(1);
        List<SPlantCustomerStockDto> sPlantCustomerStockDtoList = futureProductImeRepository.findIDVivoCustomerStockData(dateStart,dateEnd);
        cacheUtils.initCacheInput(sPlantCustomerStockDtoList);
        return sPlantCustomerStockDtoList;
    }


    public List<SPlantCustomerStockDetailDto> getVivoCustomerStockDetailData(String date){
        LocalDate dateStart = LocalDateUtils.parse(date).minusYears(1);
        LocalDate dateEnd = LocalDateUtils.parse(date).plusDays(1);
        List<SPlantCustomerStockDetailDto> sPlantCustomerStockDetailDtoList = futureProductImeRepository.findVivoCustomerStockDetailData(dateStart,dateEnd);
        cacheUtils.initCacheInput(sPlantCustomerStockDetailDtoList);
        return sPlantCustomerStockDetailDtoList;
    }

    public List<SPlantCustomerStockDetailDto> getIDVivoCustomerStockDetailData(String date){
        LocalDate dateStart = LocalDateUtils.parse(date).minusYears(1);
        LocalDate dateEnd = LocalDateUtils.parse(date).plusDays(1);
        List<SPlantCustomerStockDetailDto> sPlantCustomerStockDetailDtoList = futureProductImeRepository.findIDVivoCustomerStockDetailData(dateStart,dateEnd);
        cacheUtils.initCacheInput(sPlantCustomerStockDetailDtoList);
        return sPlantCustomerStockDetailDtoList;
    }

    public List<OppoCustomerStock> getFutureOppoCustomerStock(String date){
        if(StringUtils.isEmpty(date)){
            date=LocalDateUtils.format(LocalDate.now());
        }
        String dateStart= LocalDateUtils.format(LocalDateUtils.parse(date).plusMonths(-12));
        String dateEnd=LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        List<OppoCustomerStock> oppoCustomerStocks=futureProductImeRepository.findAll(dateStart,dateEnd,date);
        return oppoCustomerStocks;
    }

}
