package com.jsite.common.enums;

/**
 * 操作人类型枚举
 *
 * @author jsite
 */
public enum OperatorType {

    /**
     * 其他
     */
    OTHER(0, "其他"),

    /**
     * 后台用户
     */
    MANAGE(1, "后台用户"),

    /**
     * 手机端用户
     */
    MOBILE(2, "手机端用户");

    private final Integer code;
    private final String info;

    OperatorType(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
