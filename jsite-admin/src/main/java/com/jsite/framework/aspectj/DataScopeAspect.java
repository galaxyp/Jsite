package com.jsite.framework.aspectj;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jsite.common.annotation.DataScope;
import com.jsite.common.core.domain.BaseEntity;
import com.jsite.common.enums.DataScopeType;
import com.jsite.system.domain.SysRole;
import com.jsite.system.domain.SysUser;
import com.jsite.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据过滤处理
 */
@Aspect
@Component
@RequiredArgsConstructor
public class DataScopeAspect {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    private final ISysRoleService roleService;

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
        // 获取当前的用户
        SysUser currentUser = getCurrentUser();
        if (ObjectUtil.isNotNull(currentUser) && !currentUser.isAdmin()) {
            String permission = controllerDataScope.permission();
            dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
                    controllerDataScope.userAlias(), permission);
        }
    }

    /**
     * 数据范围过滤
     *
     * @param joinPoint  切点
     * @param user       用户
     * @param deptAlias  部门别名
     * @param userAlias  用户别名
     * @param permission 权限字符
     */
    public void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias, String permission) {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<>();

        // 获取用户角色
        List<SysRole> roles = roleService.selectRolesByUserId(user.getUserId());

        for (SysRole role : roles) {
            String dataScope = role.getDataScope();
            if (DATA_SCOPE_ALL.equals(dataScope)) {
                sqlString = new StringBuilder();
                conditions.clear();
                break;
            } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                sqlString.append(StrUtil.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = {} ) ",
                        deptAlias, role.getRoleId()));
            } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                sqlString.append(StrUtil.format(" OR {}.dept_id = {} ", deptAlias, user.getDeptId()));
            } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                sqlString.append(StrUtil.format(
                        " OR {}.dept_id IN ( SELECT dept_id FROM sys_dept WHERE dept_id = {} or find_in_set( {} , ancestors ) )",
                        deptAlias, user.getDeptId(), user.getDeptId()));
            } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                if (StrUtil.isNotBlank(userAlias)) {
                    sqlString.append(StrUtil.format(" OR {}.user_id = {} ", userAlias, user.getUserId()));
                } else {
                    // 数据权限为仅本人且没有userAlias别名不查询任何数据
                    sqlString.append(StrUtil.format(" OR {}.dept_id = 0 ", deptAlias));
                }
            }
        }

        // 多角色情况下，所有角色都不包含传递过来的权限字符，这个时候sqlString也会为空，所以要限制一下,不查询任何数据
        if (CollUtil.isEmpty(roles)) {
            sqlString.append(StrUtil.format(" OR {}.dept_id = 0 ", deptAlias));
        }

        if (StrUtil.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (ObjectUtil.isNotNull(params) && params instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (ObjectUtil.isNotNull(params) && params instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }

    /**
     * 获取当前用户
     */
    private SysUser getCurrentUser() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            SysUser user = new SysUser();
            user.setUserId(userId);
            // 从session获取用户信息
            Object obj = StpUtil.getSession().get("loginUser");
            if (obj instanceof SysUser) {
                return (SysUser) obj;
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}
