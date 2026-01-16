package com.jsite.generator.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsite.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成业务表字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gen_table_column")
public class GenTableColumn extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long columnId;

    /**
     * 归属表编号
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列描述
     */
    private String columnComment;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * JAVA类型
     */
    private String javaType;

    /**
     * JAVA字段名
     */
    private String javaField;

    /**
     * 是否主键（1是）
     */
    private String isPk;

    /**
     * 是否自增（1是）
     */
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    private String isQuery;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 排序
     */
    private Integer sort;

    public boolean isPk() {
        return "1".equals(this.isPk);
    }

    public boolean isIncrement() {
        return "1".equals(this.isIncrement);
    }

    public boolean isRequired() {
        return "1".equals(this.isRequired);
    }

    public boolean isInsert() {
        return "1".equals(this.isInsert);
    }

    public boolean isEdit() {
        return "1".equals(this.isEdit);
    }

    public boolean isList() {
        return "1".equals(this.isList);
    }

    public boolean isQuery() {
        return "1".equals(this.isQuery);
    }

    public boolean isSuperColumn() {
        return isSuperColumn(this.javaField);
    }

    public static boolean isSuperColumn(String javaField) {
        return StrUtil.equalsAny(javaField,
                "createBy", "createTime", "updateBy", "updateTime", "remark",
                "params", "delFlag", "parentName", "parentId", "orderNum", "ancestors");
    }

    public boolean isUsableColumn() {
        return isUsableColumn(this.javaField);
    }

    public static boolean isUsableColumn(String javaField) {
        return StrUtil.equalsAny(javaField, "parentId", "orderNum", "remark");
    }

    public String getCapJavaField() {
        return StrUtil.upperFirst(this.javaField);
    }

    public boolean isStringType() {
        return "String".equals(this.javaType);
    }
}
