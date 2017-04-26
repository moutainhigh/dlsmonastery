package net.myspring.cloud.modules.report.web;

import com.google.common.collect.Lists;
import net.myspring.cloud.common.enums.CharEnum;
import net.myspring.cloud.common.enums.DateFormat;
import net.myspring.cloud.modules.report.dto.PayableForDetailDto;
import net.myspring.cloud.modules.report.dto.PayableForSummaryDto;
import net.myspring.cloud.modules.report.service.PayableReportService;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "cloud/payableReport")
public class PayableReconciliationController {
    @Autowired
    private PayableReportService payableReportService;

    @RequestMapping(value = "summaryList")
    public String summaryList(Model model,String dateRange, String companyName){
        LocalDate dateStart = LocalDate.now().minusDays(7L);
        LocalDate dateEnd = LocalDate.now().minusDays(1L);
        if (StringUtils.isNotEmpty(dateRange)) {
            String[] dates = dateRange.split(CharEnum.WAVE_LINE.getValue());
            dateStart = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
            dateEnd = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
        }
        List<PayableForSummaryDto> payableSummaryList = Lists.newArrayList();
        if (StringUtils.isNotBlank(companyName)) {
            payableSummaryList = payableReportService.getSummaryList(dateStart, dateEnd);
        }
        model.addAttribute("payableSummary", payableSummaryList);
        model.addAttribute("dateRange", dateStart + CharEnum.WAVE_LINE.getValue() + dateEnd);
        model.addAttribute("companyName",companyName);
        return "cloud/payableSummaryList";
    }

    @RequestMapping(value = "detailList")
    public String detailList(Model model,String companyName,String supplyId,String departmentId,String dateRange){
        String[] dates = dateRange.split(CharEnum.WAVE_LINE.getValue());
        LocalDate dateStart = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
        LocalDate dateEnd = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
        List<PayableForDetailDto> payableForDetailList = Lists.newArrayList();
        if (StringUtils.isNotBlank(companyName)) {
            payableForDetailList = payableReportService.getDetailList(dateStart, dateEnd,supplyId,departmentId);
        }
        model.addAttribute("payableDetailList",payableForDetailList);
        return "cloud/payableDetailList";
    }

    @RequestMapping(value = "exportSummary")
    public ModelAndView exportSummary(String dateRange,String companyName,String supplyIdList){
        String[] supplyIds = supplyIdList.split(CharEnum.COMMA.getValue());
        List<PayableForSummaryDto> summaryDataList = Lists.newArrayList();
        List<PayableForSummaryDto> exportSummaryList = Lists.newArrayList();
        LocalDate dateStart = LocalDate.now().minusDays(7L);
        LocalDate dateEnd = LocalDate.now().minusDays(1L);
        if (StringUtils.isNotEmpty(dateRange)) {
            String[] dates = dateRange.split(CharEnum.WAVE_LINE.getValue());
            dateStart = LocalDate.parse(dates[0], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
            dateEnd = LocalDate.parse(dates[1], DateTimeFormatter.ofPattern(DateFormat.DATE.getValue()));
        }
        if (StringUtils.isNotBlank(companyName)) {
            summaryDataList = payableReportService.getSummaryList(dateStart, dateEnd);
        }
        for(String supplyId : supplyIds){
            for(PayableForSummaryDto summaryData : summaryDataList){
                if(supplyId.equals(summaryData.getSupplierId())){
                    exportSummaryList.add(summaryData);
                }
            }
        }
        return null;
    }
}
