package com.jsite.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.jsite.common.constant.Constants;
import com.jsite.common.exception.ServiceException;
import com.jsite.common.utils.SecurityUtils;
import com.jsite.common.utils.ServletUtils;
import com.jsite.system.domain.SysLogininfor;
import com.jsite.system.domain.SysUser;
import com.jsite.system.service.ISysConfigService;
import com.jsite.system.service.ISysLogininforService;
import com.jsite.system.service.ISysLoginService;
import com.jsite.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginServiceImpl implements ISysLoginService {

    private final ISysUserService userService;
    private final ISysConfigService configService;
    private final ISysLogininforService logininforService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String login(String username, String password, String code, String uuid) {
        // 验证码校验
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {
            validateCaptcha(code, uuid);
        }

        // 获取用户信息
        SysUser user = userService.selectUserByUserName(username);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            recordLogininfor(username, Constants.FAIL, "用户不存在或密码错误");
            throw new ServiceException("用户不存在或密码错误");
        }

        // 校验用户状态
        if ("1".equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            recordLogininfor(username, Constants.FAIL, "用户已被停用");
            throw new ServiceException("用户已被停用，请联系管理员");
        }

        // 校验密码
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) {
            log.info("登录用户：{} 密码错误.", username);
            recordLogininfor(username, Constants.FAIL, "密码错误");
            throw new ServiceException("用户不存在或密码错误");
        }

        // 登录成功，记录信息
        recordLoginInfo(user.getUserId());
        recordLogininfor(username, Constants.SUCCESS, "登录成功");

        // 使用Sa-Token登录
        StpUtil.login(user.getUserId());

        // 存储用户信息到session
        StpUtil.getSession().set(Constants.LOGIN_USER_KEY, user);
        // 存储登录信息到session供在线用户查询使用
        storeSessionInfo();

        return StpUtil.getTokenValue();
    }

    @Override
    public SysUser getInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = userService.selectUserById(userId);
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException("用户信息不存在");
        }
        return user;
    }

    @Override
    public void logout() {
        try {
            StpUtil.logout();
        } catch (Exception e) {
            log.error("退出登录异常：{}", e.getMessage());
        }
    }

    @Override
    public void register(String username, String password) {
        // 校验用户名是否已存在
        SysUser user = userService.selectUserByUserName(username);
        if (ObjectUtil.isNotNull(user)) {
            throw new ServiceException("用户名已存在");
        }

        // 创建新用户
        SysUser newUser = new SysUser();
        newUser.setLoginName(username);
        newUser.setUserName(username);
        newUser.setPassword(SecurityUtils.encryptPassword(password));
        newUser.setStatus("0");

        boolean success = userService.registerUser(newUser);
        if (!success) {
            throw new ServiceException("注册失败，请稍后重试");
        }
    }

    @Override
    public void recordLoginInfo(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setLoginIp(ServletUtils.getClientIP());
        user.setLoginDate(LocalDateTime.now());
        userService.updateUserProfile(user);
    }

    /**
     * 校验验证码
     */
    private void validateCaptcha(String code, String uuid) {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = (String) redisTemplate.opsForValue().get(verifyKey);
        redisTemplate.delete(verifyKey);

        if (captcha == null) {
            throw new ServiceException("验证码已失效");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new ServiceException("验证码错误");
        }
    }

    /**
     * 记录登录日志
     */
    private void recordLogininfor(String username, String status, String message) {
        try {
            String ip = ServletUtils.getClientIP();
            String userAgentStr = ServletUtils.getRequest().getHeader("User-Agent");
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);

            SysLogininfor logininfor = new SysLogininfor();
            logininfor.setUserName(username);
            logininfor.setIpaddr(ip);
            logininfor.setLoginLocation("内网IP");
            logininfor.setBrowser(userAgent != null ? userAgent.getBrowser().getName() : "Unknown");
            logininfor.setOs(userAgent != null ? userAgent.getOs().getName() : "Unknown");
            logininfor.setStatus(status);
            logininfor.setMsg(message);
            logininfor.setLoginTime(LocalDateTime.now());

            logininforService.insertLogininfor(logininfor);
        } catch (Exception e) {
            log.error("记录登录日志异常：{}", e.getMessage());
        }
    }

    /**
     * 存储session信息供在线用户查询
     */
    private void storeSessionInfo() {
        try {
            String ip = ServletUtils.getClientIP();
            String userAgentStr = ServletUtils.getRequest().getHeader("User-Agent");
            UserAgent userAgent = UserAgentUtil.parse(userAgentStr);

            StpUtil.getSession().set("ipaddr", ip);
            StpUtil.getSession().set("loginLocation", "内网IP");
            StpUtil.getSession().set("browser", userAgent != null ? userAgent.getBrowser().getName() : "Unknown");
            StpUtil.getSession().set("os", userAgent != null ? userAgent.getOs().getName() : "Unknown");
        } catch (Exception e) {
            log.error("存储session信息异常：{}", e.getMessage());
        }
    }
}
