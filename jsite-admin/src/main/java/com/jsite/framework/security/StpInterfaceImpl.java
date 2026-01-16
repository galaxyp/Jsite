package com.jsite.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.jsite.common.constant.Constants;
import com.jsite.modules.system.entity.SysUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限认证接口实现
 *
 * @author jsite
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /**
     * 获取用户权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissions = new ArrayList<>();
        try {
            SysUser user = (SysUser) StpUtil.getSession().get("user");
            if (user != null) {
                // 管理员拥有所有权限
                if (Constants.SUPER_ADMIN_ID.equals(user.getId())) {
                    permissions.add(Constants.ALL_PERMISSION);
                } else {
                    // 获取用户权限列表
                    permissions = user.getPermissions();
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return permissions;
    }

    /**
     * 获取用户角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();
        try {
            SysUser user = (SysUser) StpUtil.getSession().get("user");
            if (user != null) {
                // 管理员拥有所有角色
                if (Constants.SUPER_ADMIN_ID.equals(user.getId())) {
                    roles.add(Constants.SUPER_ADMIN_ROLE_KEY);
                } else {
                    // 获取用户角色列表
                    roles = user.getRoles();
                }
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return roles;
    }
}
