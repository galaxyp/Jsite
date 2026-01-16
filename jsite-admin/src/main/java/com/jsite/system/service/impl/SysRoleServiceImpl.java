package com.jsite.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsite.common.core.page.PageQuery;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.exception.ServiceException;
import com.jsite.common.utils.SecurityUtils;
import com.jsite.system.domain.*;
import com.jsite.system.mapper.*;
import com.jsite.system.service.ISysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色 业务层处理
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleDeptMapper roleDeptMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public TableDataInfo<SysRole> selectPageRoleList(SysRole role) {
        Page<SysRole> page = new Page<>(PageQuery.getPageNum(), PageQuery.getPageSize());
        Page<SysRole> result = roleMapper.selectRoleList(page, role);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        Page<SysRole> page = new Page<>(1, Integer.MAX_VALUE);
        return roleMapper.selectRoleList(page, role).getRecords();
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        List<SysRole> userRoles = roleMapper.selectRolesByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> perms = roleMapper.selectRolesByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (ObjectUtil.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysRole> selectRoleAll() {
        return roleMapper.selectRoleAll();
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId).stream().map(SysRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    public SysRole selectRoleById(Long roleId) {
        return roleMapper.selectRoleById(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        Long roleId = ObjectUtil.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        if (ObjectUtil.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        Long roleId = ObjectUtil.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (ObjectUtil.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public void checkRoleAllowed(SysRole role) {
        if (ObjectUtil.isNotNull(role.getRoleId()) && role.isAdmin()) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    @Override
    public void checkRoleDataScope(Long roleId) {
        if (!SysUser.isAdmin(SecurityUtils.getUserId())) {
            SysRole role = new SysRole();
            role.setRoleId(roleId);
            List<SysRole> roles = selectRoleList(role);
            if (roles.isEmpty()) {
                throw new ServiceException("没有权限访问角色数据！");
            }
        }
    }

    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        // 新增角色信息
        roleMapper.insert(role);
        return insertRoleMenu(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateById(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    @Override
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int authDataScope(SysRole role) {
        // 修改角色信息
        roleMapper.updateById(role);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    /**
     * 新增角色菜单信息
     */
    private int insertRoleMenu(SysRole role) {
        int rows = 1;
        List<SysRoleMenu> list = new ArrayList<>();
        Long[] menuIds = role.getMenuIds();
        if (ObjectUtil.isNotEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(role.getRoleId());
                rm.setMenuId(menuId);
                list.add(rm);
            }
            if (!list.isEmpty()) {
                rows = roleMenuMapper.batchRoleMenu(list);
            }
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     */
    private int insertRoleDept(SysRole role) {
        int rows = 1;
        List<SysRoleDept> list = new ArrayList<>();
        Long[] deptIds = role.getDeptIds();
        if (ObjectUtil.isNotEmpty(deptIds)) {
            for (Long deptId : deptIds) {
                SysRoleDept rd = new SysRoleDept();
                rd.setRoleId(role.getRoleId());
                rd.setDeptId(deptId);
                list.add(rd);
            }
            if (!list.isEmpty()) {
                rows = roleDeptMapper.batchRoleDept(list);
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long roleId) {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(roleId);
        return roleMapper.deleteById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配，不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(roleIds);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDept(roleIds);
        return roleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    @Override
    public int deleteAuthUser(SysUserRole userRole) {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        List<SysUserRole> list = new ArrayList<>();
        for (Long userId : userIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
}
