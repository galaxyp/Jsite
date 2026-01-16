package com.jsite.system.service;

import com.jsite.system.domain.SysUser;

/**
 * 登录服务
 */
public interface ISysLoginService {

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code     验证码
     * @param uuid     唯一标识
     * @return token
     */
    String login(String username, String password, String code, String uuid);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    SysUser getInfo();

    /**
     * 退出登录
     */
    void logout();

    /**
     * 注册
     *
     * @param username 用户名
     * @param password 密码
     */
    void register(String username, String password);

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    void recordLoginInfo(Long userId);
}
