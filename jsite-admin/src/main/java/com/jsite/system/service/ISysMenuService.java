package com.jsite.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsite.system.domain.SysMenu;
import com.jsite.system.domain.vo.RouterVo;

import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 根据用户查询系统菜单列表
     */
    List<SysMenu> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, Long userId);

    /**
     * 根据用户ID查询权限
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     */
    Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 根据菜单ID查询信息
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     */
    boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     */
    int insertMenu(SysMenu menu);

    /**
     * 修改保存菜单信息
     */
    int updateMenu(SysMenu menu);

    /**
     * 删除菜单管理信息
     */
    int deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 构建前端路由所需要的菜单
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要树结构
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     */
    List<SysMenu> buildMenuTreeSelect(List<SysMenu> menus);
}
