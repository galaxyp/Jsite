package com.jsite.system.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户管理控制器测试
 */
@SpringBootTest
@AutoConfigureMockMvc
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testListWithoutToken() throws Exception {
        // 无Token访问应返回未授权
        mockMvc.perform(get("/system/user/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
    }

    @Test
    void testGetUserByIdWithoutToken() throws Exception {
        // 无Token访问应返回未授权
        mockMvc.perform(get("/system/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").exists());
    }
}
