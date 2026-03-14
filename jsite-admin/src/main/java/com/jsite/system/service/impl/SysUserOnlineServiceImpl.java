package com.jsite.system.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.jsite.common.constant.Constants;
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
        try {
            // 获取所有已登录的会话 id（即 userId 字符串列表）
            List<String> sessionIds = StpUtil.searchSessionId("", 0, -1, false);
            for (String sessionId : sessionIds) {
                SaSession session = StpUtil.getSessionBySessionId(sessionId);
                if (session == null) continue;
                // 获取该用户的所有有效 token
                Object loginId = session.getId();
                List<String> tokenList;
                try {
                    tokenList = StpUtil.getTokenValueListByLoginId(loginId);
                } catch (Exception e) {
                    tokenList = new ArrayList<>();
                }
                if (tokenList.isEmpty()) {
                    // 没有有效 token 就跳过
                    continue;
                }
                // 每个 token 对应一条在线记录
                for (String tokenValue : tokenList) {
                    SysUserOnline userOnline = convertToUserOnline(session, tokenValue);
                    if (userOnline != null) {
                        userOnlineList.add(userOnline);
                    }
                }
            }
        } catch (Exception e) {
            // searchSessionId 不支持时返回空列表
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
     *
     * @param session    用户 Account Session
     * @param tokenValue 该次登录的真实 token 值
     */
    private SysUserOnline convertToUserOnline(SaSession session, String tokenValue) {
        SysUserOnline userOnline = new SysUserOnline();
        try {
            // 用正确的 key 取出用户对象
            Object obj = session.get(Constants.LOGIN_USER_KEY);
            if (obj instanceof SysUser) {
                SysUser user = (SysUser) obj;
                userOnline.setUserName(user.getUserName());
                userOnline.setDeptName(user.getDept() != null ? user.getDept().getDeptName() : "");
            } else if (obj != null) {
                // 兼容序列化后的 LinkedHashMap 等类型
                userOnline.setUserName(obj.toString());
            }
            // 使用真实 token 值作为会话编号，强退时需要
            userOnline.setTokenId(tokenValue);
            // 从 session 获取登录时附加的信息
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
