package com.jsite.generator.util;

import cn.hutool.core.util.StrUtil;
import com.jsite.generator.domain.GenTable;
import com.jsite.generator.domain.GenTableColumn;

import java.util.Arrays;

/**
 * 代码生成器工具类
 */
public class GenUtils {

    /**
     * 初始化表信息
     */
    public static void initTable(GenTable genTable, String operName) {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setPackageName("com.jsite");
        genTable.setModuleName(getModuleName(genTable.getTableName()));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(replaceText(genTable.getTableComment()));
        genTable.setFunctionAuthor("jsite");
        genTable.setCreateBy(operName);
        genTable.setTplCategory("crud");
    }

    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenTableColumn column, GenTable table) {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        column.setTableId(table.getTableId());
        column.setCreateBy(table.getCreateBy());
        // 设置java字段名
        column.setJavaField(StrUtil.toCamelCase(columnName));
        // 设置默认类型
        column.setJavaType("String");
        column.setQueryType("EQ");

        if (arraysContains(GenConstants.COLUMNTYPE_STR, dataType) || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType)) {
            // 字符串长度超过500设置为文本域
            Integer columnLength = getColumnLength(column.getColumnType());
            String htmlType = columnLength >= 500 || arraysContains(GenConstants.COLUMNTYPE_TEXT, dataType) ? "textarea" : "input";
            column.setHtmlType(htmlType);
        } else if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType)) {
            column.setJavaType("LocalDateTime");
            column.setHtmlType("datetime");
        } else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType)) {
            column.setHtmlType("input");
            // 如果是浮点型
            String[] str = StrUtil.split(StrUtil.subBetween(column.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0) {
                column.setJavaType("BigDecimal");
            } else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 10) {
                column.setJavaType("Integer");
            } else {
                column.setJavaType("Long");
            }
        }

        // 插入字段（默认所有字段都需要插入）
        column.setIsInsert("1");

        // 编辑字段
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_EDIT, columnName) && !column.isPk()) {
            column.setIsEdit("1");
        }
        // 列表字段
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_LIST, columnName) && !column.isPk()) {
            column.setIsList("1");
        }
        // 查询字段
        if (!arraysContains(GenConstants.COLUMNNAME_NOT_QUERY, columnName) && !column.isPk()) {
            column.setIsQuery("1");
        }

        // 查询字段类型
        if (StrUtil.endWithIgnoreCase(columnName, "name")) {
            column.setQueryType("LIKE");
        }
        // 状态字段设置单选框
        if (StrUtil.endWithIgnoreCase(columnName, "status")) {
            column.setHtmlType("radio");
        }
        // 类型&性别字段设置下拉框
        else if (StrUtil.endWithIgnoreCase(columnName, "type") || StrUtil.endWithIgnoreCase(columnName, "sex")) {
            column.setHtmlType("select");
        }
        // 图片字段设置图片上传控件
        else if (StrUtil.endWithIgnoreCase(columnName, "image")) {
            column.setHtmlType("imageUpload");
        }
        // 文件字段设置文件上传控件
        else if (StrUtil.endWithIgnoreCase(columnName, "file")) {
            column.setHtmlType("fileUpload");
        }
        // 内容字段设置富文本控件
        else if (StrUtil.endWithIgnoreCase(columnName, "content")) {
            column.setHtmlType("editor");
        }
    }

    /**
     * 校验数组是否包含指定值
     */
    public static boolean arraysContains(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取模块名
     */
    public static String getModuleName(String tableName) {
        int lastIndex = tableName.lastIndexOf("_");
        int nameLength = tableName.length();
        return StrUtil.sub(tableName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     */
    public static String getBusinessName(String tableName) {
        int firstIndex = tableName.indexOf("_");
        int nameLength = tableName.length();
        return StrUtil.sub(tableName, firstIndex + 1, nameLength);
    }

    /**
     * 表名转换成Java类名
     */
    public static String convertClassName(String tableName) {
        boolean autoRemovePre = true;
        String tablePrefix = "sys_";
        if (autoRemovePre && StrUtil.isNotEmpty(tablePrefix)) {
            String[] prefixes = tablePrefix.split(",");
            for (String prefix : prefixes) {
                if (tableName.startsWith(prefix)) {
                    tableName = tableName.substring(prefix.length());
                    break;
                }
            }
        }
        return StrUtil.upperFirst(StrUtil.toCamelCase(tableName));
    }

    /**
     * 关键字替换
     */
    public static String replaceText(String text) {
        return StrUtil.replaceChars(text, "表", "");
    }

    /**
     * 获取数据库类型字段
     */
    public static String getDbType(String columnType) {
        if (StrUtil.indexOf(columnType, '(') > 0) {
            return StrUtil.subBefore(columnType, "(", false);
        } else {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     */
    public static Integer getColumnLength(String columnType) {
        if (StrUtil.indexOf(columnType, '(') > 0) {
            String length = StrUtil.subBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        } else {
            return 0;
        }
    }
}
