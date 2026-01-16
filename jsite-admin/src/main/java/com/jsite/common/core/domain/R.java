package com.jsite.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果封装
 *
 * @author jsite
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL = 500;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功
     */
    public static <T> R<T> ok() {
        return new R<>(SUCCESS, "操作成功", null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(SUCCESS, "操作成功", data);
    }

    /**
     * 成功（带消息和数据）
     */
    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS, msg, data);
    }

    /**
     * 失败
     */
    public static <T> R<T> fail() {
        return new R<>(FAIL, "操作失败", null);
    }

    /**
     * 失败（带消息）
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(FAIL, msg, null);
    }

    /**
     * 失败（带状态码和消息）
     */
    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg, null);
    }

    /**
     * 根据条件返回结果
     */
    public static <T> R<T> toAjax(int rows) {
        return rows > 0 ? ok() : fail();
    }

    /**
     * 根据条件返回结果
     */
    public static <T> R<T> toAjax(boolean result) {
        return result ? ok() : fail();
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return SUCCESS == this.code;
    }

    /**
     * 判断是否失败
     */
    public boolean isFail() {
        return !isSuccess();
    }
}
