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
        // 测试角色名称唯一性检查
        boolean exists = roleService.checkRoleNameUnique(new SysRole() {{
            setRoleName("超级管理员");
        }});
        assertTrue(exists);
    }

    @Test
    void testCheckRoleKeyUnique() {
        // 测试角色权限字符唯一性检查
        boolean exists = roleService.checkRoleKeyUnique(new SysRole() {{
            setRoleKey("admin");
        }});
        assertTrue(exists);
    }
}
