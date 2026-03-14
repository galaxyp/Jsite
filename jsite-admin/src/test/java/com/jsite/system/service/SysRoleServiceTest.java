package com.jsite.system.service;

import com.jsite.system.domain.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色服务测试
 */
@SpringBootTest
@Transactional
class SysRoleServiceTest {

    @Autowired
    private ISysRoleService roleService;

    @Test
    void testSelectRoleList() {
        SysRole query = new SysRole();
        List<SysRole> list = roleService.selectRoleList(query);
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void testSelectRoleById() {
        // 查询超级管理员角色
        SysRole role = roleService.selectRoleById(1L);
        assertNotNull(role);
        assertEquals("超级管理员", role.getRoleName());
    }

    @Test
    void testSelectRolesByUserId() {
        // 查询管理员用户的角色
        List<SysRole> roles = roleService.selectRolesByUserId(1L);
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    void testCheckRoleNameUnique() {
        // checkRoleNameUnique 返回 true 表示"名称可用（唯一）"，false 表示"已被占用（不唯一）"
        // 场景1：已存在的角色名，不传 roleId（视为新增），应返回 false（已被占用）
        SysRole existing = new SysRole();
        existing.setRoleName("超级管理员");
        assertFalse(roleService.checkRoleNameUnique(existing), "已存在的角色名称应不唯一");

        // 场景2：不存在的角色名，应返回 true（可用）
        SysRole newRole = new SysRole();
        newRole.setRoleName("不存在的角色名_xyz_999");
        assertTrue(roleService.checkRoleNameUnique(newRole), "不存在的角色名称应唯一");
    }

    @Test
    void testCheckRoleKeyUnique() {
        // checkRoleKeyUnique 返回 true 表示"权限字符可用（唯一）"，false 表示"已被占用（不唯一）"
        // 场景1：已存在的权限字符 admin，不传 roleId（视为新增），应返回 false（已被占用）
        SysRole existing = new SysRole();
        existing.setRoleKey("admin");
        assertFalse(roleService.checkRoleKeyUnique(existing), "已存在的角色权限字符应不唯一");

        // 场景2：不存在的权限字符，应返回 true（可用）
        SysRole newRole = new SysRole();
        newRole.setRoleKey("not_exist_role_key_xyz_999");
        assertTrue(roleService.checkRoleKeyUnique(newRole), "不存在的角色权限字符应唯一");
    }
}
