package net.myspring.basic.modules.sys.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.myspring.basic.common.enums.OfficeTypeEnum;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.sys.domain.Office;
import net.myspring.basic.modules.sys.domain.OfficeBusiness;
import net.myspring.basic.modules.sys.domain.OfficeRule;
import net.myspring.basic.modules.sys.mapper.OfficeBusinessMapper;
import net.myspring.basic.modules.sys.mapper.OfficeMapper;
import net.myspring.basic.modules.sys.mapper.OfficeRuleMapper;
import net.myspring.common.constant.CharConstant;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by admin on 2017/4/6.
 */
@Component
public class OfficeManager {

    @Autowired
    private OfficeMapper officeMapper;
    @Autowired
    private OfficeRuleMapper officeRuleMapper;
    @Autowired
    private OfficeBusinessMapper officeBusinessMapper;
    @Value("${setting.adminIdList}")
    private String adminIdList;

    public List<String> officeFilter(String officeId) {
        List<String> officeIdList = Lists.newArrayList();
        if (!StringUtils.getSplitList(adminIdList, CharConstant.COMMA).contains(RequestUtils.getAccountId())) {
            Office office = officeMapper.findOne(officeId);
            officeIdList.add(office.getId());
            if (OfficeTypeEnum.BUSINESS.name().equalsIgnoreCase(office.getType())) {
                officeIdList.addAll(CollectionUtil.extractToList(officeMapper.findByParentIdsLike(office.getParentId()), "id"));
            } else {
                List<OfficeBusiness> businessList = officeBusinessMapper.findBusinessIdById(office.getId());
                if (CollectionUtil.isNotEmpty(businessList)) {
                    List<String> officeIds = CollectionUtil.extractToList(businessList, "id");
                    officeIdList.addAll(officeIds);
                    List<Office> childOfficeList = officeMapper.findByParentIdsListLike(officeIds);
                    officeIdList.addAll(CollectionUtil.extractToList(childOfficeList, "id"));
                }
            }
            if (CollectionUtil.isNotEmpty(officeIdList)) {
                officeIdList.add("0");
            }
        }
        officeIdList = Lists.newArrayList(Sets.newHashSet(officeIdList));
        return officeIdList;
    }

    public String findByOfficeIdAndRuleName(String officeId, String ruleName) {
        String id = null;
        OfficeRule officeRule = officeRuleMapper.findByName(ruleName);
        if (officeRule != null) {
            id = findByOfficeIdAndRuleId(officeId, officeRule.getId());
        }
        return id;
    }

    public String findByOfficeIdAndRuleId(String officeId, String ruleId) {
        Office office = officeMapper.findByOfficeIdAndRuleId(officeId, ruleId);
        if (office != null) {
            return office.getId();
        }
        return null;
    }
}
