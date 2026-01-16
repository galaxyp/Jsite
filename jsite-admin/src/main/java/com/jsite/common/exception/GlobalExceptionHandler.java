package com.jsite.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.jsite.common.constant.HttpStatus;
import com.jsite.common.core.domain.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author jsite
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public R<Void> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("业务异常: {} - {}", request.getRequestURI(), e.getMessage());
        Integer code = e.getCode();
        return code != null ? R.fail(code, e.getMessage()) : R.fail(e.getMessage());
    }

    /**
     * 处理未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供Token";
            case NotLoginException.INVALID_TOKEN -> "Token无效";
            case NotLoginException.TOKEN_TIMEOUT -> "Token已过期";
            case NotLoginException.BE_REPLACED -> "账号已在其他设备登录";
            case NotLoginException.KICK_OUT -> "账号已被踢下线";
            default -> "未登录";
        };
        log.error("未登录异常: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * 处理无权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        log.error("无权限异常: {} - 缺少权限: {}", request.getRequestURI(), e.getPermission());
        return R.fail(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 处理无角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        log.error("无角色异常: {} - 缺少角色: {}", request.getRequestURI(), e.getRole());
        return R.fail(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 处理请求方式不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("请求方式不支持: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.BAD_METHOD, "请求方式不支持: " + e.getMethod());
    }

    /**
     * 处理路径变量缺失异常
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public R<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        log.error("路径变量缺失: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.BAD_REQUEST, "请求路径缺少必要参数: " + e.getVariableName());
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("参数类型不匹配: {} - {}", request.getRequestURI(), e.getMessage());
        return R.fail(HttpStatus.BAD_REQUEST, "参数类型不匹配: " + e.getName());
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("请求地址不存在: {}", request.getRequestURI());
        return R.fail(HttpStatus.NOT_FOUND, "请求地址不存在");
    }

    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数校验异常: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("参数绑定异常: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理约束违反异常（@RequestParam、@PathVariable）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.error("约束违反异常: {} - {}", request.getRequestURI(), message);
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail("系统繁忙，请稍后再试");
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        return R.fail("系统繁忙，请稍后再试");
    }
}
