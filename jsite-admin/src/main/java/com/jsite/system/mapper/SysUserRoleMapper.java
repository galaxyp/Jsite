package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 通过用户ID删除用户和角色关联
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 批量删除用户和角色关联
     */
    int deleteUserRole(@Param("ids") Long[] ids);

    /**
     * 通过角色ID查询角色使用数量
     */
    int countUserRoleByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量新增用户角色信息
     */
    int batchUserRole(@Param("list") List<SysUserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     */
    int deleteUserRoleInfo(@Param("userRole") SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     */
    int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") Long[] userIds);
}
