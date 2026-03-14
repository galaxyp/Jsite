package com.jsite.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsite.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表 sys_post
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
public class SysPost extends BaseEntity {

    /**
     * 表中无此列时避免继承 BaseEntity 的 @TableLogic 参与 SQL（与 sys_menu 处理一致）
     */
    @TableField(exist = false)
    private String delFlag;

    /**
     * 岗位ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long postId;

    /**
     * 岗位编码
     */
    private String postCode;

    /**
     * 岗位名称
     */
    private String postName;

    /**
     * 显示顺序
     */
    private Integer postSort;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}
