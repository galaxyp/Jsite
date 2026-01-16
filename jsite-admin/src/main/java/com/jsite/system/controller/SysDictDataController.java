package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.jsite.common.annotation.Log;
import com.jsite.common.core.controller.BaseController;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.enums.BusinessType;
import com.jsite.system.domain.SysDictData;
import com.jsite.system.service.ISysDictDataService;
import com.jsite.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据字典信息
 */
@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController extends BaseController {

    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;

    /**
     * 获取字典数据列表
     */
    @Operation(summary = "获取字典数据列表")
    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    public TableDataInfo<SysDictData> list(SysDictData dictData) {
        return dictDataService.selectPageDictDataList(dictData);
    }

    /**
     * 查询字典数据详细
     */
    @Operation(summary = "获取字典数据详情")
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public R<SysDictData> getInfo(@PathVariable Long dictCode) {
        return R.ok(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @SaIgnore
    @Operation(summary = "根据字典类型查询字典数据信息")
    @GetMapping(value = "/type/{dictType}")
    public R<List<SysDictData>> dictType(@PathVariable String dictType) {
        SysDictData dictData = new SysDictData();
        dictData.setDictType(dictType);
        return R.ok(dictDataService.selectDictDataList(dictData));
    }

    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据")
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictData dict) {
        dict.setCreateBy(getUsername());
        return toAjax(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典数据
     */
    @Operation(summary = "修改字典数据")
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictData dict) {
        dict.setUpdateBy(getUsername());
        return toAjax(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据")
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return R.ok();
    }
}
