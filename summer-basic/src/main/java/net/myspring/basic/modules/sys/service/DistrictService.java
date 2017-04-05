package net.myspring.basic.modules.sys.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.modules.sys.domain.District;
import net.myspring.basic.modules.sys.dto.DistrictDto;
import net.myspring.basic.modules.sys.manager.DistrictManager;
import net.myspring.basic.modules.sys.mapper.DistrictMapper;
import net.myspring.util.mapper.BeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistrictService {

    @Autowired
    private DistrictManager districtManager;
    @Autowired
    private DistrictMapper districtMapper;
    @Autowired
    private CacheUtils cacheUtils;

    public District findOne(String id){
        District district=districtManager.findOne(id);
        return district;
    }

    public List<DistrictDto> findByNameLike(String name){
        List<District> citys = districtMapper.findByNameLike(name);
        List<DistrictDto> districtDtos= BeanMapper.convertDtoList(citys,DistrictDto.class);
        cacheUtils.initCacheInput(districtDtos);
        return districtDtos;
    }

    public List<District> findByProvince(){
        return districtMapper.findByProvince();
    }

}
