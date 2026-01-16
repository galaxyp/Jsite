package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.common.annotation.Log;
import com.jsite.common.core.controller.BaseController;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.enums.BusinessType;
import com.jsite.system.domain.SysLogininfor;
import com.jsite.system.service.ISysLogininforService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统访问记录
 */
@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/monitor/logininfor")
@RequiredArgsConstructor
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    /**
     * 获取登录日志列表
     */
    @Operation(summary = "获取登录日志列表")
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininfor> list(SysLogininfor logininfor) {
        Page<SysLogininfor> page = logininforService.selectLogininforPage(logininfor, getPage());
        return getDataTable(page);
    }

    /**
     * 删除登录日志
     */
    @Operation(summary = "删除登录日志")
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清空登录日志
     */
    @Operation(summary = "清空登录日志")
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }
}
