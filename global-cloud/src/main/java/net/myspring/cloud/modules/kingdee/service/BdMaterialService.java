package net.myspring.cloud.modules.kingdee.service;

import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.cloud.modules.kingdee.repository.BdMaterialRepository;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by lihx on 2017/5/12.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class BdMaterialService {
    @Autowired
    private BdMaterialRepository bdMaterialRepository;

    public BdMaterial findByName(String name){
        if (StringUtils.isNotBlank(name)){
            return bdMaterialRepository.findByName(name);
        }
        return null;
    }

    public BdMaterial findByNumber(String number){
        if (StringUtils.isNotBlank(number)){
            return bdMaterialRepository.findByNumber(number);
        }
        return null;
    }

    public List<BdMaterial> findAll(){
        return bdMaterialRepository.findAll();
    }

    public List<BdMaterial> findByMaxModifyDate(LocalDateTime modifyDate){
        return bdMaterialRepository.findByMaxModifyDate(modifyDate);
    }

}
