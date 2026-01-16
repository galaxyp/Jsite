package com.jsite.system.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.jsite.system.domain.SysUser;
import com.jsite.system.domain.SysUserOnline;
import com.jsite.system.service.ISysUserOnlineService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线用户 服务层处理
 */
@Service
public class SysUserOnlineServiceImpl implements ISysUserOnlineService {

    /**
     * 通过登录地址查询信息
     *
     * @param ipaddr   登录地址
     * @param userName 用户名
     * @return 在线用户信息
     */
    @Override
    public List<SysUserOnline> selectOnlineByIpaddr(String ipaddr, String userName) {
        List<SysUserOnline> userOnlineList = selectOnlineAll();
        List<SysUserOnline> result = new ArrayList<>();
        for (SysUserOnline userOnline : userOnlineList) {
            boolean matchIp = StrUtil.isBlank(ipaddr) || StrUtil.equals(ipaddr, userOnline.getIpaddr());
            boolean matchUser = StrUtil.isBlank(userName) || StrUtil.equals(userName, userOnline.getUserName());
            if (matchIp && matchUser) {
                result.add(userOnline);
            }
        }
        return result;
    }

    /**
     * 通过用户名称查询信息
     *
     * @param userName 用户名称
     * @return 在线用户信息集合
     */
    @Override
    public List<SysUserOnline> selectOnlineByUserName(String userName) {
        List<SysUserOnline> userOnlineList = selectOnlineAll();
        List<SysUserOnline> result = new ArrayList<>();
        for (SysUserOnline userOnline : userOnlineList) {
            if (StrUtil.equals(userName, userOnline.getUserName())) {
                result.add(userOnline);
            }
        }
        return result;
    }

    /**
     * 查询所有在线用户
     *
     * @return 在线用户集合
     */
    @Override
    public List<SysUserOnline> selectOnlineAll() {
        List<SysUserOnline> userOnlineList = new ArrayList<>();
        // 获取所有已登录的会话id
        List<String> sessionIds = StpUtil.searchSessionId("", 0, -1, false);
        for (String sessionId : sessionIds) {
            SaSession session = StpUtil.getSessionBySessionId(sessionId);
            if (session != null) {
                SysUserOnline userOnline = convertToUserOnline(session);
                if (userOnline != null) {
                    userOnlineList.add(userOnline);
                }
            }
        }
        return userOnlineList;
    }

    /**
     * 强退用户
     *
     * @param tokenId 会话ID
     */
    @Override
    public void forceLogout(String tokenId) {
        StpUtil.kickoutByTokenValue(tokenId);
    }

    /**
     * 转换为在线用户对象
     */
    private SysUserOnline convertToUserOnline(SaSession session) {
        SysUserOnline userOnline = new SysUserOnline();
        try {
            Object obj = session.get("loginUser");
            if (obj instanceof SysUser) {
                SysUser user = (SysUser) obj;
                userOnline.setUserName(user.getUserName());
                userOnline.setDeptName(user.getDept() != null ? user.getDept().getDeptName() : "");
            }
            // 获取token信息
            List<String> tokenList = session.getTokenValueList();
            if (!tokenList.isEmpty()) {
                userOnline.setTokenId(tokenList.get(0));
            }
            // 从session获取其他信息
            Object ipaddr = session.get("ipaddr");
            if (ipaddr != null) {
                userOnline.setIpaddr(ipaddr.toString());
            }
            Object loginLocation = session.get("loginLocation");
            if (loginLocation != null) {
                userOnline.setLoginLocation(loginLocation.toString());
            }
            Object browser = session.get("browser");
            if (browser != null) {
                userOnline.setBrowser(browser.toString());
            }
            Object os = session.get("os");
            if (os != null) {
                userOnline.setOs(os.toString());
            }
            userOnline.setLoginTime(session.getCreateTime());
            return userOnline;
        } catch (Exception e) {
            return null;
        }
    }
}
