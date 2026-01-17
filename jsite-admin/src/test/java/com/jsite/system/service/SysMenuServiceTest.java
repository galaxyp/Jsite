package com.jsite.system.service;

import com.jsite.system.domain.SysMenu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 菜单服务测试
 */
@SpringBootTest
@Transactional
class SysMenuServiceTest {

    @Autowired
    private ISysMenuService menuService;

    @Test
    void testSelectMenuList() {
        SysMenu query = new SysMenu();
        List<SysMenu> list = menuService.selectMenuList(query, 1L);
        assertNotNull(list);
    }

    @Test
    void testSelectMenuById() {
        // 查询系统管理菜单
        SysMenu menu = menuService.selectMenuById(1L);
        assertNotNull(menu);
    }

    @Test
    void testSelectMenuPermsByUserId() {
        // 查询管理员用户的权限
        Set<String> perms = menuService.selectMenuPermsByUserId(1L);
        assertNotNull(perms);
    }

    @Test
    void testSelectMenuTreeByUserId() {
        // 查询用户菜单树
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(1L);
        assertNotNull(menus);
    }

    @Test
    void testBuildMenus() {
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(1L);
        var routers = menuService.buildMenus(menus);
        assertNotNull(routers);
    }
}
