package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.common.annotation.Log;
import com.jsite.common.core.controller.BaseController;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.enums.BusinessType;
import com.jsite.system.domain.SysOperLog;
import com.jsite.system.service.ISysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志记录
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/monitor/operlog")
@RequiredArgsConstructor
public class SysOperlogController extends BaseController {

    private final ISysOperLogService operLogService;

    /**
     * 获取操作日志列表
     */
    @Operation(summary = "获取操作日志列表")
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/list")
    public TableDataInfo<SysOperLog> list(SysOperLog operLog) {
        Page<SysOperLog> page = operLogService.selectOperLogPage(operLog, getPage());
        return getDataTable(page);
    }

    /**
     * 根据操作日志编号获取详细信息
     */
    @Operation(summary = "获取操作日志详细信息")
    @SaCheckPermission("monitor:operlog:query")
    @GetMapping("/{operId}")
    public R<SysOperLog> getInfo(@PathVariable Long operId) {
        return R.ok(operLogService.selectOperLogById(operId));
    }

    /**
     * 删除操作日志
     */
    @Operation(summary = "删除操作日志")
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{operIds}")
    public R<Void> remove(@PathVariable Long[] operIds) {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志")
    @SaCheckPermission("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        operLogService.cleanOperLog();
        return R.ok();
    }
}
