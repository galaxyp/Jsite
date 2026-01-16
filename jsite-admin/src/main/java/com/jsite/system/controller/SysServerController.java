package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jsite.common.core.domain.R;
import com.jsite.system.domain.server.Server;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控
 */
@Tag(name = "服务器监控")
@RestController
@RequestMapping("/monitor/server")
public class SysServerController {

    /**
     * 获取服务器信息
     */
    @Operation(summary = "获取服务器信息")
    @SaCheckPermission("monitor:server:list")
    @GetMapping
    public R<Server> getInfo() throws Exception {
        Server server = new Server();
        server.copyTo();
        return R.ok(server);
    }
}
