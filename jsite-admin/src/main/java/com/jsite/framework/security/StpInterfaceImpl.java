package com.jsite.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import com.jsite.common.constant.Constants;
import com.jsite.system.service.ISysMenuService;
import com.jsite.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token 权限认证接口实现
 * 直接通过 userId 查询数据库，避免 Session 类型不匹配等问题
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final ISysMenuService menuService;
    private final ISysRoleService roleService;

    /**
     * 获取用户权限列表
     * 管理员(userId=1) 返回 Sa-Token 识别的全局通配符 "*"，其他用户从菜单权限表查询
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        try {
            Long userId = Long.parseLong(loginId.toString());
            if (Constants.SUPER_ADMIN_ID.equals(userId)) {
                // Sa-Token 的全通配符是 "*"，不是 "*:*:*"
                return List.of("*");
            }
            Set<String> perms = menuService.selectMenuPermsByUserId(userId);
            return new ArrayList<>(perms);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取用户角色列表
     * 管理员(userId=1) 返回 admin，其他用户从角色表查询
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            Long userId = Long.parseLong(loginId.toString());
            if (Constants.SUPER_ADMIN_ID.equals(userId)) {
                return List.of(Constants.SUPER_ADMIN_ROLE_KEY);
            }
            Set<String> roles = roleService.selectRolePermissionByUserId(userId);
            return new ArrayList<>(roles);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
