package com.jsite.framework.mybatis;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jsite.modules.system.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 *
 * @author jsite
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始插入填充...");

        // 填充创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        // 填充更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 填充创建者
        this.strictInsertFill(metaObject, "createBy", this::getUsername, String.class);
        // 填充更新者
        this.strictInsertFill(metaObject, "updateBy", this::getUsername, String.class);
        // 填充删除标志
        this.strictInsertFill(metaObject, "delFlag", () -> "0", String.class);
    }

    /**
     * 更新时填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始更新填充...");

        // 填充更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 填充更新者
        this.strictUpdateFill(metaObject, "updateBy", this::getUsername, String.class);
    }

    /**
     * 获取当前登录用户名
     */
    private String getUsername() {
        try {
            if (StpUtil.isLogin()) {
                SysUser user = (SysUser) StpUtil.getSession().get("user");
                return user != null ? user.getUserName() : "";
            }
        } catch (Exception e) {
            log.debug("获取当前用户失败: {}", e.getMessage());
        }
        return "";
    }
}
