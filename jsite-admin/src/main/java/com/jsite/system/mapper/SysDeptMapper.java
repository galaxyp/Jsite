package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门管理 数据层
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询部门管理数据
     */
    List<SysDept> selectDeptList(@Param("dept") SysDept dept);

    /**
     * 根据角色ID查询部门树信息
     */
    List<Long> selectDeptListByRoleId(@Param("roleId") Long roleId, @Param("deptCheckStrictly") boolean deptCheckStrictly);

    /**
     * 根据部门ID查询信息
     */
    SysDept selectDeptById(@Param("deptId") Long deptId);

    /**
     * 根据ID查询所有子部门
     */
    List<SysDept> selectChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     */
    int selectNormalChildrenDeptById(@Param("deptId") Long deptId);

    /**
     * 是否存在子节点
     */
    int hasChildByDeptId(@Param("deptId") Long deptId);

    /**
     * 查询部门是否存在用户
     */
    int checkDeptExistUser(@Param("deptId") Long deptId);

    /**
     * 校验部门名称是否唯一
     */
    SysDept checkDeptNameUnique(@Param("deptName") String deptName, @Param("parentId") Long parentId);

    /**
     * 修改子元素关系
     */
    int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 修改所在部门正常状态
     */
    void updateDeptStatusNormal(@Param("deptIds") Long[] deptIds);
}
