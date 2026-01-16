package com.jsite.generator.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsite.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 代码生成业务表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gen_table")
public class GenTable extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long tableId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 关联子表的表名
     */
    private String subTableName;

    /**
     * 子表关联的外键名
     */
    private String subTableFkName;

    /**
     * 实体类名称
     */
    private String className;

    /**
     * 使用的模板（crud单表操作 tree树表操作）
     */
    private String tplCategory;

    /**
     * 生成包路径
     */
    private String packageName;

    /**
     * 生成模块名
     */
    private String moduleName;

    /**
     * 生成业务名
     */
    private String businessName;

    /**
     * 生成功能名
     */
    private String functionName;

    /**
     * 生成功能作者
     */
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    private String genPath;

    /**
     * 其它生成选项
     */
    private String options;

    /**
     * 备注
     */
    private String remark;

    /**
     * 表列信息
     */
    @TableField(exist = false)
    private List<GenTableColumn> columns;

    /**
     * 主键信息
     */
    @TableField(exist = false)
    private GenTableColumn pkColumn;

    /**
     * 子表信息
     */
    @TableField(exist = false)
    private GenTable subTable;

    /**
     * 树编码字段
     */
    @TableField(exist = false)
    private String treeCode;

    /**
     * 树父编码字段
     */
    @TableField(exist = false)
    private String treeParentCode;

    /**
     * 树名称字段
     */
    @TableField(exist = false)
    private String treeName;

    /**
     * 上级菜单ID字段
     */
    @TableField(exist = false)
    private String parentMenuId;

    /**
     * 上级菜单名称字段
     */
    @TableField(exist = false)
    private String parentMenuName;

    public boolean isCrud() {
        return "crud".equals(this.tplCategory);
    }

    public boolean isTree() {
        return "tree".equals(this.tplCategory);
    }

    public boolean isSub() {
        return "sub".equals(this.tplCategory);
    }
}
