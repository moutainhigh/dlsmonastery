package net.myspring.future.modules.layout.web.form;

import net.myspring.common.form.DataForm;
import net.myspring.future.modules.layout.domain.AdPricesystemChange;

import java.math.BigDecimal;

/**
 * Created by zhangyf on 2017/5/11.
 */
public class AdPricesystemChangeForm extends DataForm<AdPricesystemChange>{

    private BigDecimal oldPrice;
    private BigDecimal newPrice;
}