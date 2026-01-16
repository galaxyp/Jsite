package com.jsite.common.constant;

/**
 * 缓存常量
 *
 * @author jsite
 */
public class CacheConstants {

    /**
     * 登录用户 Redis Key 前缀
     */
    public static final String LOGIN_USER_KEY = "login_user:";

    /**
     * 验证码 Redis Key 前缀
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 Cache Key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 Cache Key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 Cache Key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 Cache Key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 Cache Key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 用户权限 Cache Key
     */
    public static final String USER_PERMS_KEY = "user_perms:";

    /**
     * 用户角色 Cache Key
     */
    public static final String USER_ROLES_KEY = "user_roles:";

    /**
     * 在线用户 Cache Key
     */
    public static final String ONLINE_USER_KEY = "online_user:";
}
