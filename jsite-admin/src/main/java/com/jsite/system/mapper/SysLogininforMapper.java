package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysLogininfor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统访问日志情况信息 数据层
 */
@Mapper
public interface SysLogininforMapper extends BaseMapper<SysLogininfor> {

    /**
     * 清空系统登录日志
     */
    void cleanLogininfor();
}
