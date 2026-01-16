package com.jsite.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * 树形实体基类
 *
 * @author jsite
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TreeEntity<T> extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父级ID
     */
    @Schema(description = "父级ID")
    private Long parentId;

    /**
     * 父级名称
     */
    @Schema(description = "父级名称")
    @TableField(exist = false)
    private String parentName;

    /**
     * 祖级列表
     */
    @Schema(description = "祖级列表")
    private String ancestors;

    /**
     * 显示顺序
     */
    @Schema(description = "显示顺序")
    private Integer orderNum;

    /**
     * 子节点列表
     */
    @Schema(description = "子节点列表")
    @TableField(exist = false)
    private List<T> children = new ArrayList<>();
}
