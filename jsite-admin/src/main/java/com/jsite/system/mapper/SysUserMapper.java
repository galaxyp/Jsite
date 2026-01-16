package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsite.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 数据层
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 分页查询用户列表
     */
    IPage<SysUser> selectUserList(IPage<SysUser> page, @Param("user") SysUser user);

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUserName(@Param("userName") String userName);

    /**
     * 根据用户ID查询用户
     */
    SysUser selectUserById(@Param("userId") Long userId);

    /**
     * 根据部门ID查询用户数量
     */
    int countUserByDeptId(@Param("deptId") Long deptId);

    /**
     * 校验用户名称是否唯一
     */
    SysUser checkUserNameUnique(@Param("userName") String userName);

    /**
     * 校验手机号码是否唯一
     */
    SysUser checkPhoneUnique(@Param("phonenumber") String phonenumber);

    /**
     * 校验email是否唯一
     */
    SysUser checkEmailUnique(@Param("email") String email);
}
