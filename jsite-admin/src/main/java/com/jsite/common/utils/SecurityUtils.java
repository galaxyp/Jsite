package com.jsite.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.SecureUtil;
import com.jsite.common.constant.Constants;
import com.jsite.common.exception.ServiceException;
import com.jsite.system.domain.SysUser;

/**
 * 安全工具类
 *
 * @author jsite
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        try {
            return StpUtil.getLoginIdAsLong();
        } catch (Exception e) {
            throw new ServiceException("获取用户ID失败，请重新登录");
        }
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        try {
            SysUser user = getLoginUser();
            return user != null ? user.getUserName() : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取当前登录用户信息
     */
    public static SysUser getLoginUser() {
        try {
            return (SysUser) StpUtil.getSession().get("user");
        } catch (Exception e) {
            throw new ServiceException("获取用户信息失败，请重新登录");
        }
    }

    /**
     * 是否为管理员
     */
    public static boolean isAdmin() {
        Long userId = getUserId();
        return isAdmin(userId);
    }

    /**
     * 是否为管理员
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && Constants.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * 密码加密
     */
    public static String encryptPassword(String password) {
        return SecureUtil.md5(password);
    }

    /**
     * 密码验证
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return encryptPassword(rawPassword).equals(encodedPassword);
    }

    /**
     * 生成BCrypt密码
     */
    public static String encryptPasswordBCrypt(String password) {
        return SecureUtil.md5(SecureUtil.sha256(password));
    }

    /**
     * 判断是否已登录
     */
    public static boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 获取Token
     */
    public static String getToken() {
        return StpUtil.getTokenValue();
    }
}
