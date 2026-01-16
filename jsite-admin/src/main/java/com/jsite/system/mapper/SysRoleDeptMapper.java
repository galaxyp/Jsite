package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色与部门关联表 数据层
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {

    /**
     * 通过角色ID删除角色和部门关联
     */
    int deleteRoleDeptByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量删除角色部门关联信息
     */
    int deleteRoleDept(@Param("ids") Long[] ids);

    /**
     * 查询部门使用数量
     */
    int selectCountRoleDeptByDeptId(@Param("deptId") Long deptId);

    /**
     * 批量新增角色部门信息
     */
    int batchRoleDept(@Param("list") List<SysRoleDept> roleDeptList);
}
