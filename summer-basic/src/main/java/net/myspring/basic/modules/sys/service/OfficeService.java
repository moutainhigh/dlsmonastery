package net.myspring.basic.modules.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.myspring.basic.common.enums.OfficeTypeEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.domain.OfficeLeader;
import net.myspring.basic.modules.hr.mapper.OfficeLeaderMapper;
import net.myspring.basic.modules.sys.domain.Office;
import net.myspring.basic.modules.sys.domain.OfficeBusiness;
import net.myspring.basic.modules.sys.domain.OfficeRule;
import net.myspring.basic.modules.sys.dto.OfficeDto;
import net.myspring.basic.modules.sys.dto.OfficeRuleDto;
import net.myspring.basic.modules.sys.manager.OfficeManager;
import net.myspring.basic.modules.sys.mapper.OfficeBusinessMapper;
import net.myspring.basic.modules.sys.mapper.OfficeMapper;
import net.myspring.basic.modules.sys.mapper.OfficeRuleMapper;
import net.myspring.basic.modules.sys.web.form.OfficeForm;
import net.myspring.basic.modules.sys.web.query.OfficeQuery;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.constant.TreeConstant;
import net.myspring.common.response.RestResponse;
import net.myspring.common.tree.TreeNode;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.reflect.ReflectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    @Autowired
    private OfficeMapper officeMapper;
    @Autowired
    private OfficeManager officeManager;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private OfficeRuleMapper officeRuleMapper;
    @Autowired
    private OfficeBusinessMapper officeBusinessMapper;
    @Autowired
    private OfficeLeaderMapper officeLeaderMapper;


    public List<Office> findByOfficeRuleName(String officeRuleName) {
        return officeMapper.findByOfficeRuleName(officeRuleName);
    }

    public Page<OfficeDto> findPage(Pageable pageable, OfficeQuery officeQuery) {
        Page<OfficeDto> page = officeMapper.findPage(pageable, officeQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public List<String> getOfficeFilterIds(String officeId) {
        List<String> officeIdList =officeManager.officeFilter(officeId);
        return officeIdList;
    }

    public List<Office> findByParentIdsLike(String parentId) {
        List<Office> officeList = officeMapper.findByParentIdsLike(parentId);
        return officeList;
    }


    public List<Office> findAll() {
        return officeMapper.findAll();
    }

    public OfficeDto searchById(OfficeQuery officeQuery) {
        OfficeDto officeDto=new OfficeDto();
        List<String> officeFilter = officeManager.officeFilter(RequestUtils.getRequestEntity().getOfficeId());
        officeQuery.setOfficeIds(officeFilter);
        List<Office> officeList = officeMapper.findByFilter(officeQuery);
        if(CollectionUtil.isNotEmpty(officeList)){
            officeDto= BeanUtil.map(officeList.get(0),OfficeDto.class);
            cacheUtils.initCacheInput(officeDto);
        }
        return officeDto;
    }

    public List<String> getSameAreaByOfficeId(String officeId){
        List<Office> sameAreaByOfficeId = officeMapper.findSameAreaByOfficeId(officeId);
        return CollectionUtil.extractToList(sameAreaByOfficeId,"id");
    }

    public OfficeDto findOne(OfficeDto officeDto) {
        if (!officeDto.isCreate()) {
            Office office = officeMapper.findOne(officeDto.getId());
            officeDto = BeanUtil.map(office, OfficeDto.class);
            if (OfficeTypeEnum.SUPPORT.name().equals(office.getType())) {
                List<OfficeBusiness> businessOffices = officeBusinessMapper.findBusinessIdById(office.getId());
                officeDto.setOfficeTree(getOfficeTree(CollectionUtil.extractToList(businessOffices, "id")));
            }
            List<OfficeLeader> officeLeaderList = officeLeaderMapper.findByOfficeId(officeDto.getId());
            officeDto.setLeaderIdList(CollectionUtil.extractToList(officeLeaderList, "leaderId"));
            cacheUtils.initCacheInput(officeDto);
        }
        return officeDto;
    }

    public RestResponse check(OfficeForm officeForm) {
        Office parent = officeMapper.findOne(officeForm.getParentId());
        if (OfficeTypeEnum.BUSINESS.name().equals(officeForm.getType())) {
            OfficeRule topOfficeRule = officeRuleMapper.findTopOfficeRule();
            OfficeRule officeRule = officeRuleMapper.findOne(officeForm.getOfficeRuleId());
            if (parent != null && topOfficeRule.getId().equals(officeForm.getOfficeRuleId())) {
                return new RestResponse("顶级业务部门不能设置上级", null,false);
            } else if (parent != null) {
                if (!officeRule.getParentId().equals(parent.getOfficeRuleId())) {
                    return new RestResponse("业务部门上级类型不正确", null,false);
                }
            } else {
                return new RestResponse("非顶级业务部门必须设置上级", null,false);
            }
        }
        officeForm.setParent(parent);
        return new RestResponse("验证成功", null);
    }

    public Office save(OfficeForm officeForm) {
        Office office;
        if(officeForm.getParent()!=null){
            OfficeRule officeRule=officeRuleMapper.findTopOfficeRule();
            officeForm.setAreaId(officeManager.getOfficeIdByOfficeRule(officeForm.getParent().getId(),officeRule.getId()));
        }
        if (officeForm.isCreate()) {
            office = BeanUtil.map(officeForm, Office.class);
            officeMapper.save(office);
            if(officeForm.getParent()==null){
                officeForm.setAreaId(office.getId());
            }
            officeMapper.update(office);
        } else {
            if(officeForm.getParent()==null){
                officeForm.setAreaId(officeForm.getId());
            }
            officeManager.officeFilter(officeForm.getId());
            office = officeMapper.findOne(officeForm.getId());
            String oldParentIds=office.getParentIds();
            ReflectionUtil.copyProperties(officeForm, office);
            officeMapper.update(office);
            List<Office> list = officeMapper.findByParentIdsLike("%," + office.getId() + ",%");
            for (Office item : list) {
                item.setParentIds(item.getParentIds().replace(oldParentIds, office.getParentIds()));
                officeMapper.update(item);
            }
        }

        List<OfficeBusiness> businessOfficeList = officeBusinessMapper.findAllBusinessIdById(office.getId());
        if (OfficeTypeEnum.SUPPORT.name().equals(officeForm.getType())&&CollectionUtil.isNotEmpty(officeForm.getOfficeIdList())) {
            List<String> businessOfficeIdList = CollectionUtil.extractToList(businessOfficeList, "businessOfficeId");
            List<String> removeIdList = CollectionUtil.subtract(businessOfficeIdList, officeForm.getOfficeIdList());
            List<String> addIdList = CollectionUtil.subtract(officeForm.getOfficeIdList(), businessOfficeIdList);
            List<OfficeBusiness> officeBusinessList = Lists.newArrayList();
            for (String businessOfficeId : addIdList) {
                officeBusinessList.add(new OfficeBusiness(office.getId(), businessOfficeId));
            }
            officeBusinessMapper.setEnabledByOfficeId(true, office.getId());
            if (CollectionUtil.isNotEmpty(removeIdList)) {
                officeBusinessMapper.setEnabledByBusinessOfficeIds(removeIdList);
            }
            if (CollectionUtil.isNotEmpty(addIdList)) {
                officeBusinessMapper.batchSave(officeBusinessList);
            }
        } else if (CollectionUtil.isNotEmpty(businessOfficeList)) {
            officeBusinessMapper.setEnabledByOfficeId(false, office.getId());
        }

        List<OfficeLeader> officeLeaderList=officeLeaderMapper.findAllByOfficeId(office.getId());
        if (CollectionUtil.isNotEmpty(officeForm.getLeaderIdList())) {
            List<String> officeLeaderIdList = CollectionUtil.extractToList(officeLeaderList, "leaderId");
            List<String> removeIdList = CollectionUtil.subtract(officeLeaderIdList, officeForm.getLeaderIdList());
            List<String> addIdList = CollectionUtil.subtract(officeForm.getLeaderIdList(), officeLeaderIdList);
            List<OfficeLeader> leaderList = Lists.newArrayList();
            for (String leaderId : officeForm.getLeaderIdList()) {
                leaderList.add(new OfficeLeader(office.getId(), leaderId));
            }
            officeLeaderMapper.setEnabledByOfficeId(true, office.getId());
            if (CollectionUtil.isNotEmpty(removeIdList)) {
                officeLeaderMapper.setEnabledByLeaderIds(false,removeIdList);
            }
            if (CollectionUtil.isNotEmpty(addIdList)) {
                officeLeaderMapper.batchSave(leaderList);
            }
        }else if(CollectionUtil.isNotEmpty(officeLeaderList)){
            officeLeaderMapper.setEnabledByOfficeId(false,office.getId());
        }
        return office;
    }

    public void logicDeleteOne(Office office) {
        officeMapper.logicDeleteOne(office.getId());
    }

    public List<OfficeDto> findByFilter(OfficeQuery officeQuery) {
        officeQuery.setOfficeIds(officeManager.officeFilter(RequestUtils.getRequestEntity().getOfficeId()));
        List<Office> officeList = officeMapper.findByFilter(officeQuery);
        List<OfficeDto> officeDtoList = BeanUtil.map(officeList, OfficeDto.class);
        cacheUtils.initCacheInput(officeDtoList);
        return officeDtoList;
    }

    public List<OfficeBusiness> findBusinessIdById(String id) {
        return officeBusinessMapper.findBusinessIdById(id);
    }

    public List<OfficeRuleDto> findOfficeRuleList() {
        List<OfficeRule> officeRuleList = officeRuleMapper.findAllEnabled();
        List<OfficeRuleDto> officeRuleDtoList = BeanUtil.map(officeRuleList, OfficeRuleDto.class);
        return officeRuleDtoList;
    }

    public TreeNode getOfficeTree(List<String> officeIdList) {
        TreeNode treeNode = new TreeNode("0", "部门列表");
        List<Office> officeList = officeMapper.findAll();
        getTreeNodeList(officeList, treeNode.getChildren(), TreeConstant.ROOT_PARENT_IDS);
        treeNode.setChecked(Lists.newArrayList(Sets.newHashSet(officeIdList)));
        return treeNode;
    }

    public void getTreeNodeList(List<Office> officeList, List<TreeNode> childList, String parentIds) {
        for (Office office : officeList) {
            if (parentIds.equalsIgnoreCase(office.getParentIds())) {
                TreeNode treeNode = new TreeNode(office.getId(), office.getName());
                childList.add(treeNode);
                getTreeNodeList(officeList, treeNode.getChildren(), office.getParentIds() + office.getId() + CharConstant.COMMA);
            }
        }
    }
}
