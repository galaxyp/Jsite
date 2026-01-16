package com.jsite.common.constant;

/**
 * 通用常量
 *
 * @author jsite
 */
public class Constants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌名称
     */
    public static final String TOKEN = "token";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 防重提交 Redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 Redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 自动识别json对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "com.jsite"};

    /**
     * 正常状态
     */
    public static final String NORMAL = "0";

    /**
     * 异常状态
     */
    public static final String EXCEPTION = "1";

    /**
     * 用户正常状态
     */
    public static final String USER_NORMAL = "0";

    /**
     * 用户禁用状态
     */
    public static final String USER_DISABLE = "1";

    /**
     * 角色正常状态
     */
    public static final String ROLE_NORMAL = "0";

    /**
     * 角色禁用状态
     */
    public static final String ROLE_DISABLE = "1";

    /**
     * 部门正常状态
     */
    public static final String DEPT_NORMAL = "0";

    /**
     * 部门禁用状态
     */
    public static final String DEPT_DISABLE = "1";

    /**
     * 字典正常状态
     */
    public static final String DICT_NORMAL = "0";

    /**
     * 是否为系统默认（是）
     */
    public static final String YES = "Y";

    /**
     * 是否为系统默认（否）
     */
    public static final String NO = "N";

    /**
     * 是否菜单外链（是）
     */
    public static final String YES_FRAME = "0";

    /**
     * 是否菜单外链（否）
     */
    public static final String NO_FRAME = "1";

    /**
     * 菜单类型（目录）
     */
    public static final String TYPE_DIR = "M";

    /**
     * 菜单类型（菜单）
     */
    public static final String TYPE_MENU = "C";

    /**
     * 菜单类型（按钮）
     */
    public static final String TYPE_BUTTON = "F";

    /**
     * Layout组件标识
     */
    public static final String LAYOUT = "Layout";

    /**
     * ParentView组件标识
     */
    public static final String PARENT_VIEW = "ParentView";

    /**
     * InnerLink组件标识
     */
    public static final String INNER_LINK = "InnerLink";

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 超级管理员角色Key
     */
    public static final String SUPER_ADMIN_ROLE_KEY = "admin";

    /**
     * 所有权限标识
     */
    public static final String ALL_PERMISSION = "*:*:*";
}
