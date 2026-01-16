package com.jsite.system.service;

import com.jsite.system.domain.SysUserOnline;

import java.util.List;

/**
 * 在线用户 服务层
 */
public interface ISysUserOnlineService {

    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr   登录地址
     * @param userName 用户名
     * @return 在线用户信息
     */
    List<SysUserOnline> selectOnlineByIpaddr(String ipaddr, String userName);

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @return 在线用户信息集合
     */
    List<SysUserOnline> selectOnlineByUserName(String userName);

    /**
     * 查询所有在线用户
     *
     * @return 在线用户集合
     */
    List<SysUserOnline> selectOnlineAll();

    /**
     * 强退用户
     *
     * @param tokenId 会话ID
     */
    void forceLogout(String tokenId);
}
