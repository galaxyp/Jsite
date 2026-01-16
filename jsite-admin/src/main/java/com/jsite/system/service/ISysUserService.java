package com.jsite.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.system.domain.SysUser;

import java.util.List;

/**
 * 用户 业务层
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 分页查询用户列表
     */
    TableDataInfo<SysUser> selectPageUserList(SysUser user);

    /**
     * 根据条件查询用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 通过用户名查询用户
     */
    SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     */
    SysUser selectUserById(Long userId);

    /**
     * 根据用户ID查询用户所属角色组
     */
    String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     */
    String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     */
    boolean checkUserNameUnique(SysUser user);

    /**
     * 校验手机号码是否唯一
     */
    boolean checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     */
    boolean checkEmailUnique(SysUser user);

    /**
     * 校验用户是否允许操作
     */
    void checkUserAllowed(SysUser user);

    /**
     * 校验用户是否有数据权限
     */
    void checkUserDataScope(Long userId);

    /**
     * 新增用户信息
     */
    int insertUser(SysUser user);

    /**
     * 注册用户信息
     */
    boolean registerUser(SysUser user);

    /**
     * 修改用户信息
     */
    int updateUser(SysUser user);

    /**
     * 用户授权角色
     */
    void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 修改用户状态
     */
    int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     */
    int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     */
    boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     */
    int resetPwd(SysUser user);

    /**
     * 重置用户密码
     */
    int resetUserPwd(String userName, String password);

    /**
     * 通过用户ID删除用户
     */
    int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     */
    int deleteUserByIds(Long[] userIds);

    /**
     * 导入用户数据
     */
    String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);
}
