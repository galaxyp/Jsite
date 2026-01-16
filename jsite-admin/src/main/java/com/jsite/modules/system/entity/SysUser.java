package com.jsite.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsite.common.core.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体
 *
 * @author jsite
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@Schema(description = "用户信息")
public class SysUser extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 登录账号
     */
    @Schema(description = "登录账号")
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 30, message = "登录账号长度不能超过30个字符")
    private String loginName;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    @NotBlank(message = "用户昵称不能为空")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String userName;

    /**
     * 用户类型（sys系统用户）
     */
    @Schema(description = "用户类型")
    private String userType;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    private String phone;

    /**
     * 用户性别（0未知 1男 2女）
     */
    @Schema(description = "用户性别")
    private String sex;

    /**
     * 头像路径
     */
    @Schema(description = "头像路径")
    private String avatar;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 盐加密
     */
    @Schema(hidden = true)
    @JsonIgnore
    private String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Schema(description = "帐号状态")
    private String status;

    /**
     * 最后登录IP
     */
    @Schema(description = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间")
    private LocalDateTime loginTime;

    /**
     * 密码最后更新时间
     */
    @Schema(description = "密码最后更新时间")
    private LocalDateTime pwdUpdateTime;

    /**
     * 部门对象
     */
    @Schema(description = "部门信息")
    @TableField(exist = false)
    private SysDept dept;

    /**
     * 角色列表
     */
    @Schema(description = "角色列表")
    @TableField(exist = false)
    private List<String> roles;

    /**
     * 权限列表
     */
    @Schema(description = "权限列表")
    @TableField(exist = false)
    private List<String> permissions;

    /**
     * 角色ID列表
     */
    @Schema(description = "角色ID列表")
    @TableField(exist = false)
    private List<Long> roleIds;

    /**
     * 岗位ID列表
     */
    @Schema(description = "岗位ID列表")
    @TableField(exist = false)
    private List<Long> postIds;

    /**
     * 是否管理员
     */
    public boolean isAdmin() {
        return this.id != null && 1L == this.id;
    }
}
