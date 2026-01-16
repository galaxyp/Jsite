package com.jsite.generator.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.common.annotation.Log;
import com.jsite.common.core.controller.BaseController;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.enums.BusinessType;
import com.jsite.generator.domain.GenTable;
import com.jsite.generator.domain.GenTableColumn;
import com.jsite.generator.service.IGenTableColumnService;
import com.jsite.generator.service.IGenTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代码生成 控制器
 */
@Tag(name = "代码生成")
@RestController
@RequestMapping("/tool/gen")
@RequiredArgsConstructor
public class GenController extends BaseController {

    private final IGenTableService genTableService;
    private final IGenTableColumnService genTableColumnService;

    /**
     * 查询代码生成列表
     */
    @Operation(summary = "查询代码生成列表")
    @SaCheckPermission("tool:gen:list")
    @GetMapping("/list")
    public TableDataInfo<GenTable> genList(GenTable genTable) {
        Page<GenTable> page = genTableService.selectGenTablePage(genTable, getPage());
        return getDataTable(page);
    }

    /**
     * 修改代码生成业务
     */
    @Operation(summary = "获取代码生成详情")
    @SaCheckPermission("tool:gen:query")
    @GetMapping("/{tableId}")
    public R<Map<String, Object>> getInfo(@PathVariable Long tableId) {
        GenTable table = genTableService.selectGenTableById(tableId);
        List<GenTableColumn> columns = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        Map<String, Object> result = new HashMap<>();
        result.put("info", table);
        result.put("rows", columns);
        return R.ok(result);
    }

    /**
     * 查询数据库列表
     */
    @Operation(summary = "查询数据库列表")
    @SaCheckPermission("tool:gen:list")
    @GetMapping("/db/list")
    public TableDataInfo<GenTable> dataList(GenTable genTable) {
        Page<GenTable> page = genTableService.selectDbTablePage(genTable, getPage());
        return getDataTable(page);
    }

    /**
     * 查询数据表字段列表
     */
    @Operation(summary = "查询数据表字段列表")
    @SaCheckPermission("tool:gen:list")
    @GetMapping("/column/{tableId}")
    public TableDataInfo<GenTableColumn> columnList(@PathVariable Long tableId) {
        List<GenTableColumn> list = genTableColumnService.selectGenTableColumnListByTableId(tableId);
        return getDataTable(list);
    }

    /**
     * 导入表结构（保存）
     */
    @Operation(summary = "导入表结构")
    @SaCheckPermission("tool:gen:import")
    @Log(title = "代码生成", businessType = BusinessType.IMPORT)
    @PostMapping("/importTable")
    public R<Void> importTableSave(@RequestParam String tables) {
        String[] tableNames = tables.split(",");
        // 查询表信息
        List<GenTable> tableList = genTableService.selectDbTableListByNames(tableNames);
        String operName = StpUtil.getLoginIdAsString();
        genTableService.importGenTable(tableList, operName);
        return R.ok();
    }

    /**
     * 修改保存代码生成业务
     */
    @Operation(summary = "修改代码生成配置")
    @SaCheckPermission("tool:gen:edit")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> editSave(@RequestBody GenTable genTable) {
        genTableService.validateEdit(genTable);
        genTableService.updateGenTable(genTable);
        return R.ok();
    }

    /**
     * 删除代码生成
     */
    @Operation(summary = "删除代码生成配置")
    @SaCheckPermission("tool:gen:remove")
    @Log(title = "代码生成", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tableIds}")
    public R<Void> remove(@PathVariable Long[] tableIds) {
        genTableService.deleteGenTableByIds(tableIds);
        return R.ok();
    }

    /**
     * 预览代码
     */
    @Operation(summary = "预览代码")
    @SaCheckPermission("tool:gen:preview")
    @GetMapping("/preview/{tableId}")
    public R<Map<String, String>> preview(@PathVariable Long tableId) {
        Map<String, String> dataMap = genTableService.previewCode(tableId);
        return R.ok(dataMap);
    }

    /**
     * 生成代码（下载方式）
     */
    @Operation(summary = "下载代码")
    @SaCheckPermission("tool:gen:code")
    @Log(title = "代码生成", businessType = BusinessType.EXPORT)
    @GetMapping("/download/{tableName}")
    public void download(HttpServletResponse response, @PathVariable String tableName) throws IOException {
        byte[] data = genTableService.downloadCode(tableName);
        genCode(response, data);
    }

    /**
     * 生成代码（自定义路径）
     */
    @Operation(summary = "生成代码到自定义路径")
    @SaCheckPermission("tool:gen:code")
    @Log(title = "代码生成", businessType = BusinessType.EXPORT)
    @GetMapping("/genCode/{tableName}")
    public R<Void> genCode(@PathVariable String tableName) {
        genTableService.generatorCode(tableName);
        return R.ok();
    }

    /**
     * 同步数据库
     */
    @Operation(summary = "同步数据库")
    @SaCheckPermission("tool:gen:edit")
    @Log(title = "代码生成", businessType = BusinessType.UPDATE)
    @GetMapping("/synchDb/{tableName}")
    public R<Void> synchDb(@PathVariable String tableName) {
        genTableService.synchDb(tableName);
        return R.ok();
    }

    /**
     * 批量生成代码
     */
    @Operation(summary = "批量生成代码")
    @SaCheckPermission("tool:gen:code")
    @Log(title = "代码生成", businessType = BusinessType.EXPORT)
    @GetMapping("/batchGenCode")
    public void batchGenCode(HttpServletResponse response, @RequestParam String tables) throws IOException {
        String[] tableNames = tables.split(",");
        byte[] data = genTableService.downloadCode(tableNames);
        genCode(response, data);
    }

    /**
     * 生成zip文件
     */
    private void genCode(HttpServletResponse response, byte[] data) throws IOException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment; filename=\"jsite-code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.getOutputStream().write(data);
    }
}
