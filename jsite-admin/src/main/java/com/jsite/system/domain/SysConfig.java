package com.jsite.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jsite.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置表 sys_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    /**
     * 表中无此列，避免继承 BaseEntity 的 @TableLogic 参与 SQL
     */
    @TableField(exist = false)
    private String delFlag;

    /**
     * 参数主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long configId;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数键值
     */
    private String configValue;

    /**
     * 系统内置（Y是 N否）
     */
    private String configType;
}
