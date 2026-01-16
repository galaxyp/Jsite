package com.jsite.generator.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.common.exception.ServiceException;
import com.jsite.generator.domain.GenTable;
import com.jsite.generator.domain.GenTableColumn;
import com.jsite.generator.mapper.GenTableColumnMapper;
import com.jsite.generator.mapper.GenTableMapper;
import com.jsite.generator.service.IGenTableService;
import com.jsite.generator.util.GenConstants;
import com.jsite.generator.util.GenUtils;
import com.jsite.generator.util.VelocityInitializer;
import com.jsite.generator.util.VelocityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成 服务层实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenTableServiceImpl implements IGenTableService {

    private final GenTableMapper genTableMapper;
    private final GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务列表
     */
    @Override
    public Page<GenTable> selectGenTablePage(GenTable genTable, Page<GenTable> page) {
        LambdaQueryWrapper<GenTable> wrapper = buildQueryWrapper(genTable);
        return genTableMapper.selectPage(page, wrapper);
    }

    /**
     * 查询业务列表
     */
    @Override
    public List<GenTable> selectGenTableList(GenTable genTable) {
        LambdaQueryWrapper<GenTable> wrapper = buildQueryWrapper(genTable);
        return genTableMapper.selectList(wrapper);
    }

    private LambdaQueryWrapper<GenTable> buildQueryWrapper(GenTable genTable) {
        LambdaQueryWrapper<GenTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(genTable.getTableName()), GenTable::getTableName, genTable.getTableName())
                .like(StrUtil.isNotBlank(genTable.getTableComment()), GenTable::getTableComment, genTable.getTableComment())
                .orderByDesc(GenTable::getUpdateTime);
        return wrapper;
    }

    /**
     * 查询据库列表
     */
    @Override
    public Page<GenTable> selectDbTablePage(GenTable genTable, Page<GenTable> page) {
        List<GenTable> list = genTableMapper.selectDbTableList(genTable);
        int total = list.size();
        int fromIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        int toIndex = Math.min(fromIndex + (int) page.getSize(), total);
        List<GenTable> pageList = fromIndex < total ? list.subList(fromIndex, toIndex) : new ArrayList<>();
        page.setRecords(pageList);
        page.setTotal(total);
        return page;
    }

    /**
     * 查询据库列表
     */
    @Override
    public List<GenTable> selectDbTableList(GenTable genTable) {
        return genTableMapper.selectDbTableList(genTable);
    }

    /**
     * 查询据库列表
     */
    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        return genTableMapper.selectDbTableListByNames(tableNames);
    }

    /**
     * 查询所有表信息
     */
    @Override
    public List<GenTable> selectGenTableAll() {
        return genTableMapper.selectGenTableAll();
    }

    /**
     * 查询业务信息
     */
    @Override
    public GenTable selectGenTableById(Long id) {
        GenTable genTable = genTableMapper.selectGenTableById(id);
        setTableFromOptions(genTable);
        return genTable;
    }

    /**
     * 修改业务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGenTable(GenTable genTable) {
        String options = JSONUtil.toJsonStr(genTable.getParams());
        genTable.setOptions(options);
        genTableMapper.updateById(genTable);
        if (CollUtil.isNotEmpty(genTable.getColumns())) {
            for (GenTableColumn column : genTable.getColumns()) {
                genTableColumnMapper.updateById(column);
            }
        }
    }

    /**
     * 删除业务信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGenTableByIds(Long[] tableIds) {
        genTableMapper.deleteBatchIds(Arrays.asList(tableIds));
        for (Long tableId : tableIds) {
            genTableColumnMapper.delete(new LambdaQueryWrapper<GenTableColumn>()
                    .eq(GenTableColumn::getTableId, tableId));
        }
    }

    /**
     * 导入表结构
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importGenTable(List<GenTable> tableList, String operName) {
        for (GenTable table : tableList) {
            String tableName = table.getTableName();
            GenUtils.initTable(table, operName);
            genTableMapper.insert(table);

            // 保存列信息
            List<GenTableColumn> genTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
            for (GenTableColumn column : genTableColumns) {
                GenUtils.initColumnField(column, table);
                genTableColumnMapper.insert(column);
            }
        }
    }

    /**
     * 预览代码
     */
    @Override
    public Map<String, String> previewCode(Long tableId) {
        Map<String, String> dataMap = new LinkedHashMap<>();
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableById(tableId);
        setTableFromOptions(table);
        setPkColumn(table);
        VelocityInitializer.initVelocity();

        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            dataMap.put(template, sw.toString());
        }
        return dataMap;
    }

    /**
     * 生成代码（下载方式）
     */
    @Override
    public byte[] downloadCode(String tableName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        generatorCode(tableName, zip);
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    /**
     * 生成代码（自定义路径）
     */
    @Override
    public void generatorCode(String tableName) {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        setTableFromOptions(table);
        setPkColumn(table);

        VelocityInitializer.initVelocity();
        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            if (!StrUtil.containsAny(template, "api.ts.vm", "index.vue.vm", "index-tree.vue.vm")) {
                // 渲染模板
                StringWriter sw = new StringWriter();
                Template tpl = Velocity.getTemplate(template, "UTF-8");
                tpl.merge(context, sw);
                // TODO: 写入文件
            }
        }
    }

    /**
     * 同步数据库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchDb(String tableName) {
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        List<GenTableColumn> tableColumns = table.getColumns();
        Map<String, GenTableColumn> tableColumnMap = new HashMap<>();
        tableColumns.forEach(column -> tableColumnMap.put(column.getColumnName(), column));

        List<GenTableColumn> dbTableColumns = genTableColumnMapper.selectDbTableColumnsByName(tableName);
        if (CollUtil.isEmpty(dbTableColumns)) {
            throw new ServiceException("同步数据失败，原表结构不存在");
        }
        List<String> dbTableColumnNames = dbTableColumns.stream().map(GenTableColumn::getColumnName).toList();

        dbTableColumns.forEach(column -> {
            GenUtils.initColumnField(column, table);
            if (tableColumnMap.containsKey(column.getColumnName())) {
                GenTableColumn prevColumn = tableColumnMap.get(column.getColumnName());
                column.setColumnId(prevColumn.getColumnId());
                if (column.isList()) {
                    column.setDictType(prevColumn.getDictType());
                    column.setQueryType(prevColumn.getQueryType());
                }
                if (StrUtil.isNotEmpty(prevColumn.getIsRequired()) && !column.isPk()
                        && (column.isInsert() || column.isEdit())
                        && ((column.isUsableColumn()) || (!column.isSuperColumn()))) {
                    column.setIsRequired(prevColumn.getIsRequired());
                    column.setHtmlType(prevColumn.getHtmlType());
                }
                genTableColumnMapper.updateById(column);
            } else {
                genTableColumnMapper.insert(column);
            }
        });

        List<GenTableColumn> delColumns = tableColumns.stream()
                .filter(column -> !dbTableColumnNames.contains(column.getColumnName())).toList();
        if (CollUtil.isNotEmpty(delColumns)) {
            List<Long> delColumnIds = delColumns.stream().map(GenTableColumn::getColumnId).toList();
            genTableColumnMapper.deleteBatchIds(delColumnIds);
        }
    }

    /**
     * 批量生成代码（下载方式）
     */
    @Override
    public byte[] downloadCode(String[] tableNames) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        for (String tableName : tableNames) {
            generatorCode(tableName, zip);
        }
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    /**
     * 查询表信息并生成代码
     */
    private void generatorCode(String tableName, ZipOutputStream zip) {
        // 查询表信息
        GenTable table = genTableMapper.selectGenTableByName(tableName);
        setTableFromOptions(table);
        setPkColumn(table);

        VelocityInitializer.initVelocity();
        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = VelocityUtils.getTemplateList(table.getTplCategory());
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            try {
                // 添加到zip
                zip.putNextEntry(new ZipEntry(VelocityUtils.getFileName(template, table)));
                IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
                zip.flush();
                zip.closeEntry();
            } catch (IOException e) {
                log.error("渲染模板失败，表名：{}", table.getTableName(), e);
            }
        }
    }

    /**
     * 修改保存参数校验
     */
    @Override
    public void validateEdit(GenTable genTable) {
        if (GenConstants.TPL_TREE.equals(genTable.getTplCategory())) {
            String options = JSONUtil.toJsonStr(genTable.getParams());
            JSONObject paramsObj = JSONUtil.parseObj(options);
            if (StrUtil.isEmpty(paramsObj.getStr(GenConstants.TREE_CODE))) {
                throw new ServiceException("树编码字段不能为空");
            } else if (StrUtil.isEmpty(paramsObj.getStr(GenConstants.TREE_PARENT_CODE))) {
                throw new ServiceException("树父编码字段不能为空");
            } else if (StrUtil.isEmpty(paramsObj.getStr(GenConstants.TREE_NAME))) {
                throw new ServiceException("树名称字段不能为空");
            }
        } else if (GenConstants.TPL_SUB.equals(genTable.getTplCategory())) {
            if (StrUtil.isEmpty(genTable.getSubTableName())) {
                throw new ServiceException("关联子表的表名不能为空");
            } else if (StrUtil.isEmpty(genTable.getSubTableFkName())) {
                throw new ServiceException("子表关联的外键名不能为空");
            }
        }
    }

    /**
     * 设置主键列信息
     */
    public void setPkColumn(GenTable table) {
        for (GenTableColumn column : table.getColumns()) {
            if (column.isPk()) {
                table.setPkColumn(column);
                break;
            }
        }
        if (ObjectUtil.isNull(table.getPkColumn())) {
            table.setPkColumn(table.getColumns().get(0));
        }
        if (GenConstants.TPL_SUB.equals(table.getTplCategory())) {
            for (GenTableColumn column : table.getSubTable().getColumns()) {
                if (column.isPk()) {
                    table.getSubTable().setPkColumn(column);
                    break;
                }
            }
            if (ObjectUtil.isNull(table.getSubTable().getPkColumn())) {
                table.getSubTable().setPkColumn(table.getSubTable().getColumns().get(0));
            }
        }
    }

    /**
     * 设置代码生成其他选项值
     */
    public void setTableFromOptions(GenTable genTable) {
        JSONObject paramsObj = JSONUtil.parseObj(genTable.getOptions());
        if (ObjectUtil.isNotNull(paramsObj)) {
            String treeCode = paramsObj.getStr(GenConstants.TREE_CODE);
            String treeParentCode = paramsObj.getStr(GenConstants.TREE_PARENT_CODE);
            String treeName = paramsObj.getStr(GenConstants.TREE_NAME);
            String parentMenuId = paramsObj.getStr(GenConstants.PARENT_MENU_ID);
            String parentMenuName = paramsObj.getStr(GenConstants.PARENT_MENU_NAME);

            genTable.setTreeCode(treeCode);
            genTable.setTreeParentCode(treeParentCode);
            genTable.setTreeName(treeName);
            genTable.setParentMenuId(parentMenuId);
            genTable.setParentMenuName(parentMenuName);
        }
    }
}
