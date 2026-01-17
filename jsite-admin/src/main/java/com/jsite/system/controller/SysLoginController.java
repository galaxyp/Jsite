package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.jsite.common.core.domain.R;
import com.jsite.system.domain.SysMenu;
import com.jsite.system.domain.SysUser;
import com.jsite.system.domain.vo.LoginBody;
import com.jsite.system.domain.vo.RouterVo;
import com.jsite.system.service.ISysLoginService;
import com.jsite.system.service.ISysMenuService;
import com.jsite.system.service.ISysRoleService;
import com.jsite.common.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 登录验证
 */
@Tag(name = "登录验证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SysLoginController {

    private final ISysLoginService loginService;
    private final ISysMenuService menuService;
    private final ISysRoleService roleService;

    /**
     * 获取验证码
     */
    @SaIgnore
    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    public R<Map<String, Object>> getCaptcha() {
        Map<String, Object> result = new HashMap<>();
        // 暂时关闭验证码
        result.put("captchaEnabled", false);
        return R.ok(result);
    }

    /**
     * 登录方法
     */
    @SaIgnore
    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@Validated @RequestBody LoginBody loginBody) {
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(),
                loginBody.getCode(), loginBody.getUuid());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return R.ok(result);
    }

    /**
     * 获取用户信息
     */
    @Operation(summary = "获取用户信息")
    @GetMapping("/getInfo")
    public R<Map<String, Object>> getInfo() {
        SysUser user = loginService.getInfo();
        // 获取角色权限
        Set<String> roles = roleService.selectRolePermissionByUserId(user.getUserId());
        // 获取菜单权限
        Set<String> permissions;
        if (SysUser.isAdmin(user.getUserId())) {
            // 管理员拥有所有权限
            permissions = Set.of("*:*:*");
        } else {
            permissions = menuService.selectMenuPermsByUserId(user.getUserId());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", roles);
        result.put("permissions", permissions);
        return R.ok(result);
    }

    /**
     * 获取路由信息
     */
    @Operation(summary = "获取路由信息")
    @GetMapping("/getRouters")
    public R<List<RouterVo>> getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.ok(menuService.buildMenus(menus));
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功", null);
    }

    /**
     * 用户注册
     */
    @SaIgnore
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody LoginBody loginBody) {
        loginService.register(loginBody.getUsername(), loginBody.getPassword());
        return R.ok("注册成功", null);
    }
}
