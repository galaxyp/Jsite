package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsite.system.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色表 数据层
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 分页查询角色列表
     */
    IPage<SysRole> selectRoleList(IPage<SysRole> page, @Param("role") SysRole role);

    /**
     * 根据用户ID查询角色
     */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询角色权限
     */
    List<String> selectRolePermissionByUserId(@Param("userId") Long userId);

    /**
     * 查询所有角色
     */
    List<SysRole> selectRoleAll();

    /**
     * 根据角色ID查询角色
     */
    SysRole selectRoleById(@Param("roleId") Long roleId);

    /**
     * 校验角色名称是否唯一
     */
    SysRole checkRoleNameUnique(@Param("roleName") String roleName);

    /**
     * 校验角色权限是否唯一
     */
    SysRole checkRoleKeyUnique(@Param("roleKey") String roleKey);
}
