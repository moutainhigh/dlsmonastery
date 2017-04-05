package net.myspring.basic.modules.sys.mapper;

import net.myspring.basic.common.mybatis.MyMapper;
import net.myspring.basic.modules.sys.domain.ProcessFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcessFlowMapper extends MyMapper<ProcessFlow,String> {

    ProcessFlow findByProductTypeAndName(@Param("processTypeId") String processTypeId, @Param("name") String name);

    List<ProcessFlow> findByProcessType(String processTypeId);
}
