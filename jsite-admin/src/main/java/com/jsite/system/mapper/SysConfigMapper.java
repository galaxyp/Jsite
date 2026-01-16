package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsite.system.domain.SysConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 参数配置 数据层
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 分页查询参数配置列表
     */
    IPage<SysConfig> selectConfigList(IPage<SysConfig> page, @Param("config") SysConfig config);

    /**
     * 根据键名查询参数配置信息
     */
    SysConfig selectConfigByKey(@Param("configKey") String configKey);

    /**
     * 根据参数ID查询参数配置信息
     */
    SysConfig selectConfigById(@Param("configId") Long configId);

    /**
     * 校验参数键名是否唯一
     */
    SysConfig checkConfigKeyUnique(@Param("configKey") String configKey);
}
