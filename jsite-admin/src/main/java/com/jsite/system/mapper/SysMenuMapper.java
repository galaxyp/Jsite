package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单表 数据层
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询系统菜单列表
     */
    List<SysMenu> selectMenuList(@Param("menu") SysMenu menu);

    /**
     * 根据用户ID查询菜单
     */
    List<SysMenu> selectMenuListByUserId(@Param("menu") SysMenu menu, @Param("userId") Long userId);

    /**
     * 根据用户ID查询权限
     */
    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限
     */
    List<String> selectMenuPermsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     */
    List<SysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);

    /**
     * 查询所有菜单树信息
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId, @Param("menuCheckStrictly") boolean menuCheckStrictly);

    /**
     * 根据菜单ID查询信息
     */
    SysMenu selectMenuById(@Param("menuId") Long menuId);

    /**
     * 是否存在菜单子节点
     */
    int hasChildByMenuId(@Param("menuId") Long menuId);

    /**
     * 查询菜单是否存在角色
     */
    int checkMenuExistRole(@Param("menuId") Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    SysMenu checkMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") Long parentId);
}
