package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与菜单关联表 数据层
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 查询菜单使用数量
     */
    int checkMenuExistRole(@Param("menuId") Long menuId);

    /**
     * 通过角色ID删除角色和菜单关联
     */
    int deleteRoleMenuByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量删除角色菜单关联信息
     */
    int deleteRoleMenu(@Param("ids") Long[] ids);

    /**
     * 批量新增角色菜单信息
     */
    int batchRoleMenu(@Param("list") List<SysRoleMenu> roleMenuList);
}
