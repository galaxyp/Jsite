package com.jsite.system.service;

import com.jsite.system.domain.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 */
@SpringBootTest
@Transactional
class SysUserServiceTest {

    @Autowired
    private ISysUserService userService;

    @Test
    void testSelectUserList() {
        SysUser query = new SysUser();
        List<SysUser> list = userService.selectUserList(query);
        assertNotNull(list);
    }

    @Test
    void testSelectUserById() {
        // 查询管理员用户
        SysUser user = userService.selectUserById(1L);
        assertNotNull(user);
        assertEquals("admin", user.getLoginName());
    }

    @Test
    void testCheckUserNameUnique() {
        // checkUserNameUnique 返回 true 表示"登录名可用（唯一）"，false 表示"已被占用（不唯一）"
        // 场景1：已存在的登录名 admin，不传 userId（视为新增），应返回 false（已被占用）
        SysUser user = new SysUser();
        user.setLoginName("admin");
        assertFalse(userService.checkUserNameUnique(user), "已存在的登录名应不唯一");

        // 场景2：不存在的登录名，应返回 true（可用）
        SysUser user2 = new SysUser();
        user2.setLoginName("nonexistent_user_12345");
        assertTrue(userService.checkUserNameUnique(user2), "不存在的登录名应唯一");
    }

    @Test
    void testSelectUserByUserName() {
        SysUser user = userService.selectUserByUserName("admin");
        assertNotNull(user);
        assertEquals(1L, user.getUserId());
    }
}
