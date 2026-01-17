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
        // 测试用户名唯一性检查
        SysUser user = new SysUser();
        user.setLoginName("admin");
        boolean exists = userService.checkUserNameUnique(user);
        assertTrue(exists);

        SysUser user2 = new SysUser();
        user2.setLoginName("nonexistent_user_12345");
        boolean notExists = userService.checkUserNameUnique(user2);
        assertFalse(notExists);
    }

    @Test
    void testSelectUserByUserName() {
        SysUser user = userService.selectUserByUserName("admin");
        assertNotNull(user);
        assertEquals(1L, user.getUserId());
    }
}
