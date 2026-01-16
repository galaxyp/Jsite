package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jsite.common.annotation.Log;
import com.jsite.common.core.controller.BaseController;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.enums.BusinessType;
import com.jsite.system.domain.SysUserOnline;
import com.jsite.system.service.ISysUserOnlineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线用户监控
 */
@Tag(name = "在线用户监控")
@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class SysUserOnlineController extends BaseController {

    private final ISysUserOnlineService userOnlineService;

    /**
     * 获取在线用户列表
     */
    @Operation(summary = "获取在线用户列表")
    @SaCheckPermission("monitor:online:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserOnline> list(String ipaddr, String userName) {
        List<SysUserOnline> userOnlineList = userOnlineService.selectOnlineByIpaddr(ipaddr, userName);
        // 手动分页
        int pageNum = getPageNum();
        int pageSize = getPageSize();
        int total = userOnlineList.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<SysUserOnline> pageList = new ArrayList<>();
        if (fromIndex < total) {
            pageList = userOnlineList.subList(fromIndex, toIndex);
        }
        return getDataTable(pageList, total);
    }

    /**
     * 强退用户
     */
    @Operation(summary = "强退用户")
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public R<Void> forceLogout(@PathVariable String tokenId) {
        userOnlineService.forceLogout(tokenId);
        return R.ok();
    }
}
