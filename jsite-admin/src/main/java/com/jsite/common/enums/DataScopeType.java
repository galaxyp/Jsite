package com.jsite.common.enums;

/**
 * 数据范围类型枚举
 *
 * @author jsite
 */
public enum DataScopeType {

    /**
     * 全部数据权限
     */
    ALL("1", "全部数据权限"),

    /**
     * 自定义数据权限
     */
    CUSTOM("2", "自定义数据权限"),

    /**
     * 本部门数据权限
     */
    DEPT("3", "本部门数据权限"),

    /**
     * 本部门及以下数据权限
     */
    DEPT_AND_CHILD("4", "本部门及以下数据权限"),

    /**
     * 仅本人数据权限
     */
    SELF("5", "仅本人数据权限");

    private final String code;
    private final String info;

    DataScopeType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
