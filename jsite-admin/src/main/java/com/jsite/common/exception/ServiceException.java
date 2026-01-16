package com.jsite.common.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常
 *
 * @author jsite
 */
@Getter
public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 错误明细
     */
    private String detailMessage;

    /**
     * 空构造
     */
    public ServiceException() {
    }

    /**
     * 仅消息
     */
    public ServiceException(String message) {
        this.message = message;
    }

    /**
     * 消息和异常
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    /**
     * 错误码和消息
     */
    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设置详细消息
     */
    public ServiceException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
