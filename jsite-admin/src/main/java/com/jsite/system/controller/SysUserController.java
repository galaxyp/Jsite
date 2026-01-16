package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jsite.common.annotation.Log;
import com.jsite.common.core.controller.BaseController;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.enums.BusinessType;
import com.jsite.common.utils.SecurityUtils;
import com.jsite.system.domain.SysDept;
import com.jsite.system.domain.SysRole;
import com.jsite.system.domain.SysUser;
import com.jsite.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysDeptService deptService;
    private final ISysPostService postService;

    /**
     * 获取用户列表
     */
    @Operation(summary = "获取用户列表")
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo<SysUser> list(SysUser user) {
        return userService.selectPageUserList(user);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @Operation(summary = "获取用户详情")
    @SaCheckPermission("system:user:query")
    @GetMapping(value = {"/{userId}", "/"})
    public R<Map<String, Object>> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        userService.checkUserDataScope(userId);
        Map<String, Object> result = new HashMap<>();
        List<SysRole> roles = roleService.selectRoleAll();
        result.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        result.put("posts", postService.selectPostAll());
        if (userId != null) {
            SysUser sysUser = userService.selectUserById(userId);
            result.put("user", sysUser);
            result.put("postIds", postService.selectPostListByUserId(userId));
            result.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return R.ok(result);
    }

    /**
     * 新增用户
     */
    @Operation(summary = "新增用户")
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUser user) {
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        if (!userService.checkPhoneUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setCreateBy(getUsername());
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @Operation(summary = "修改用户")
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        if (!userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (!userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (java.util.Arrays.asList(userIds).contains(getUserId())) {
            return R.fail("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @Operation(summary = "状态修改")
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * 获取部门树列表
     */
    @Operation(summary = "获取部门树列表")
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    public R<List<SysDept>> deptTree(SysDept dept) {
        return R.ok(deptService.selectDeptTreeList(dept));
    }
}
