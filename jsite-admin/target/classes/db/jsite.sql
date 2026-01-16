-- ----------------------------
-- JSite 快速开发平台数据库初始化脚本
-- 数据库：MySQL 8.0+
-- ----------------------------

-- 创建数据库
-- CREATE DATABASE IF NOT EXISTS jsite DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- USE jsite;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id         BIGINT          DEFAULT NULL COMMENT '部门ID',
    login_name      VARCHAR(30)     NOT NULL COMMENT '登录账号',
    user_name       VARCHAR(30)     NOT NULL COMMENT '用户昵称',
    user_type       VARCHAR(10)     DEFAULT 'sys' COMMENT '用户类型（sys系统用户）',
    email           VARCHAR(50)     DEFAULT '' COMMENT '用户邮箱',
    phone           VARCHAR(11)     DEFAULT '' COMMENT '手机号码',
    sex             CHAR(1)         DEFAULT '0' COMMENT '性别（0未知 1男 2女）',
    avatar          VARCHAR(200)    DEFAULT '' COMMENT '头像路径',
    password        VARCHAR(100)    DEFAULT '' COMMENT '密码',
    salt            VARCHAR(20)     DEFAULT '' COMMENT '盐加密',
    status          CHAR(1)         DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    login_ip        VARCHAR(128)    DEFAULT '' COMMENT '最后登录IP',
    login_time      DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    pwd_update_time DATETIME        DEFAULT NULL COMMENT '密码最后更新时间',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_login_name (login_name)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户表';

-- ----------------------------
-- 2. 部门表
-- ----------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id       BIGINT          DEFAULT 0 COMMENT '父部门ID',
    ancestors       VARCHAR(500)    DEFAULT '' COMMENT '祖级列表',
    dept_name       VARCHAR(30)     DEFAULT '' COMMENT '部门名称',
    order_num       INT             DEFAULT 0 COMMENT '显示顺序',
    leader          VARCHAR(20)     DEFAULT NULL COMMENT '负责人',
    phone           VARCHAR(11)     DEFAULT NULL COMMENT '联系电话',
    email           VARCHAR(50)     DEFAULT NULL COMMENT '邮箱',
    status          CHAR(1)         DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=200 COMMENT='部门表';

-- ----------------------------
-- 3. 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name       VARCHAR(30)     NOT NULL COMMENT '角色名称',
    role_key        VARCHAR(100)    NOT NULL COMMENT '角色权限字符串',
    role_sort       INT             NOT NULL COMMENT '显示顺序',
    data_scope      CHAR(1)         DEFAULT '1' COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    status          CHAR(1)         DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色表';

-- ----------------------------
-- 4. 菜单权限表
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    menu_name       VARCHAR(50)     NOT NULL COMMENT '菜单名称',
    parent_id       BIGINT          DEFAULT 0 COMMENT '父菜单ID',
    order_num       INT             DEFAULT 0 COMMENT '显示顺序',
    path            VARCHAR(200)    DEFAULT '' COMMENT '路由地址',
    component       VARCHAR(255)    DEFAULT NULL COMMENT '组件路径',
    query           VARCHAR(255)    DEFAULT NULL COMMENT '路由参数',
    is_frame        CHAR(1)         DEFAULT '1' COMMENT '是否外链（0是 1否）',
    is_cache        CHAR(1)         DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
    menu_type       CHAR(1)         DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    visible         CHAR(1)         DEFAULT '0' COMMENT '显示状态（0显示 1隐藏）',
    status          CHAR(1)         DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    perms           VARCHAR(100)    DEFAULT NULL COMMENT '权限标识',
    icon            VARCHAR(100)    DEFAULT '#' COMMENT '菜单图标',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=2000 COMMENT='菜单权限表';

-- ----------------------------
-- 5. 用户和角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    role_id         BIGINT          NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

-- ----------------------------
-- 6. 角色和菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id         BIGINT          NOT NULL COMMENT '角色ID',
    menu_id         BIGINT          NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色菜单关联表';

-- ----------------------------
-- 7. 角色和部门关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_role_dept;
CREATE TABLE sys_role_dept (
    role_id         BIGINT          NOT NULL COMMENT '角色ID',
    dept_id         BIGINT          NOT NULL COMMENT '部门ID',
    PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB COMMENT='角色部门关联表';

-- ----------------------------
-- 8. 岗位表
-- ----------------------------
DROP TABLE IF EXISTS sys_post;
CREATE TABLE sys_post (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    post_code       VARCHAR(64)     NOT NULL COMMENT '岗位编码',
    post_name       VARCHAR(50)     NOT NULL COMMENT '岗位名称',
    post_sort       INT             NOT NULL COMMENT '显示顺序',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_post_code (post_code)
) ENGINE=InnoDB AUTO_INCREMENT=10 COMMENT='岗位表';

-- ----------------------------
-- 9. 用户和岗位关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_post;
CREATE TABLE sys_user_post (
    user_id         BIGINT          NOT NULL COMMENT '用户ID',
    post_id         BIGINT          NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (user_id, post_id)
) ENGINE=InnoDB COMMENT='用户岗位关联表';

-- ----------------------------
-- 10. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    dict_name       VARCHAR(100)    DEFAULT '' COMMENT '字典名称',
    dict_type       VARCHAR(100)    DEFAULT '' COMMENT '字典类型',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典类型表';

-- ----------------------------
-- 11. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典数据ID',
    dict_sort       INT             DEFAULT 0 COMMENT '字典排序',
    dict_label      VARCHAR(100)    DEFAULT '' COMMENT '字典标签',
    dict_value      VARCHAR(100)    DEFAULT '' COMMENT '字典键值',
    dict_type       VARCHAR(100)    DEFAULT '' COMMENT '字典类型',
    css_class       VARCHAR(100)    DEFAULT '' COMMENT '样式属性',
    list_class      VARCHAR(100)    DEFAULT '' COMMENT '表格样式',
    is_default      CHAR(1)         DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_dict_type (dict_type)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典数据表';

-- ----------------------------
-- 12. 系统配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '参数ID',
    config_name     VARCHAR(100)    DEFAULT '' COMMENT '参数名称',
    config_key      VARCHAR(100)    DEFAULT '' COMMENT '参数键名',
    config_value    VARCHAR(500)    DEFAULT '' COMMENT '参数键值',
    config_type     CHAR(1)         DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='系统配置表';

-- ----------------------------
-- 13. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    title           VARCHAR(50)     DEFAULT '' COMMENT '模块标题',
    business_type   INT             DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    method          VARCHAR(100)    DEFAULT '' COMMENT '方法名称',
    request_method  VARCHAR(10)     DEFAULT '' COMMENT '请求方式',
    operator_type   INT             DEFAULT 0 COMMENT '操作类别（0其它 1后台 2手机）',
    oper_name       VARCHAR(50)     DEFAULT '' COMMENT '操作人员',
    dept_name       VARCHAR(50)     DEFAULT '' COMMENT '部门名称',
    oper_url        VARCHAR(255)    DEFAULT '' COMMENT '请求URL',
    oper_ip         VARCHAR(128)    DEFAULT '' COMMENT '主机地址',
    oper_location   VARCHAR(255)    DEFAULT '' COMMENT '操作地点',
    oper_param      VARCHAR(2000)   DEFAULT '' COMMENT '请求参数',
    json_result     VARCHAR(2000)   DEFAULT '' COMMENT '返回参数',
    status          INT             DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    error_msg       VARCHAR(2000)   DEFAULT '' COMMENT '错误消息',
    oper_time       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    cost_time       BIGINT          DEFAULT 0 COMMENT '消耗时间（毫秒）',
    PRIMARY KEY (id),
    KEY idx_oper_time (oper_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='操作日志表';

-- ----------------------------
-- 14. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    login_name      VARCHAR(50)     DEFAULT '' COMMENT '登录账号',
    ip_addr         VARCHAR(128)    DEFAULT '' COMMENT '登录IP',
    login_location  VARCHAR(255)    DEFAULT '' COMMENT '登录地点',
    browser         VARCHAR(50)     DEFAULT '' COMMENT '浏览器类型',
    os              VARCHAR(50)     DEFAULT '' COMMENT '操作系统',
    status          CHAR(1)         DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    msg             VARCHAR(255)    DEFAULT '' COMMENT '提示消息',
    login_time      DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (id),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='登录日志表';

-- ----------------------------
-- 15. 代码生成业务表
-- ----------------------------
DROP TABLE IF EXISTS gen_table;
CREATE TABLE gen_table (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '编号',
    table_name      VARCHAR(200)    DEFAULT '' COMMENT '表名称',
    table_comment   VARCHAR(500)    DEFAULT '' COMMENT '表描述',
    sub_table_name  VARCHAR(64)     DEFAULT NULL COMMENT '关联子表的表名',
    sub_table_fk    VARCHAR(64)     DEFAULT NULL COMMENT '子表关联外键',
    class_name      VARCHAR(100)    DEFAULT '' COMMENT '实体类名称',
    tpl_category    VARCHAR(200)    DEFAULT 'crud' COMMENT '使用的模板（crud单表 tree树表 sub主子表）',
    package_name    VARCHAR(100)    DEFAULT '' COMMENT '生成包路径',
    module_name     VARCHAR(30)     DEFAULT '' COMMENT '生成模块名',
    business_name   VARCHAR(30)     DEFAULT '' COMMENT '生成业务名',
    function_name   VARCHAR(50)     DEFAULT '' COMMENT '生成功能名',
    function_author VARCHAR(50)     DEFAULT '' COMMENT '生成功能作者',
    gen_type        CHAR(1)         DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
    gen_path        VARCHAR(200)    DEFAULT '/' COMMENT '生成路径',
    options         VARCHAR(1000)   DEFAULT '' COMMENT '其它生成选项',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='代码生成业务表';

-- ----------------------------
-- 16. 代码生成列表
-- ----------------------------
DROP TABLE IF EXISTS gen_table_column;
CREATE TABLE gen_table_column (
    id              BIGINT          NOT NULL AUTO_INCREMENT COMMENT '编号',
    table_id        BIGINT          DEFAULT NULL COMMENT '归属表编号',
    column_name     VARCHAR(200)    DEFAULT '' COMMENT '列名称',
    column_comment  VARCHAR(500)    DEFAULT '' COMMENT '列描述',
    column_type     VARCHAR(100)    DEFAULT '' COMMENT '列类型',
    java_type       VARCHAR(500)    DEFAULT '' COMMENT 'Java类型',
    java_field      VARCHAR(200)    DEFAULT '' COMMENT 'Java字段名',
    is_pk           CHAR(1)         DEFAULT '0' COMMENT '是否主键（1是）',
    is_increment    CHAR(1)         DEFAULT '0' COMMENT '是否自增（1是）',
    is_required     CHAR(1)         DEFAULT '0' COMMENT '是否必填（1是）',
    is_insert       CHAR(1)         DEFAULT '0' COMMENT '是否插入字段（1是）',
    is_edit         CHAR(1)         DEFAULT '0' COMMENT '是否编辑字段（1是）',
    is_list         CHAR(1)         DEFAULT '0' COMMENT '是否列表字段（1是）',
    is_query        CHAR(1)         DEFAULT '0' COMMENT '是否查询字段（1是）',
    query_type      VARCHAR(200)    DEFAULT 'EQ' COMMENT '查询方式（EQ= NE!= GT> GE>= LT< LE<= LIKE BETWEEN）',
    html_type       VARCHAR(200)    DEFAULT '' COMMENT '显示类型（input textarea select checkbox radio datetime）',
    dict_type       VARCHAR(200)    DEFAULT '' COMMENT '字典类型',
    sort            INT             DEFAULT 0 COMMENT '排序',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_table_id (table_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='代码生成列表';

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 初始化数据
-- =====================================================

-- ----------------------------
-- 初始化部门数据
-- ----------------------------
INSERT INTO sys_dept VALUES (100, 0, '0', 'JSite科技', 0, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (101, 100, '0,100', '深圳总公司', 1, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (105, 101, '0,100,101', '测试部门', 3, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (108, 102, '0,100,102', '市场部门', 1, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');
INSERT INTO sys_dept VALUES (109, 102, '0,100,102', '财务部门', 2, '若依', '15888888888', 'jsite@qq.com', '0', 'admin', NOW(), '', NOW(), '0');

-- ----------------------------
-- 初始化用户数据（密码：admin123 -> MD5加密）
-- ----------------------------
INSERT INTO sys_user VALUES (1, 103, 'admin', '超级管理员', 'sys', 'admin@jsite.com', '15888888888', '1', '', '0192023a7bbd73250516f069df18b500', '', '0', '127.0.0.1', NOW(), NOW(), 'admin', NOW(), '', NOW(), '管理员', '0');
INSERT INTO sys_user VALUES (2, 105, 'jsite', 'JSite', 'sys', 'jsite@qq.com', '15666666666', '1', '', '0192023a7bbd73250516f069df18b500', '', '0', '127.0.0.1', NOW(), NOW(), 'admin', NOW(), '', NOW(), '测试员', '0');

-- ----------------------------
-- 初始化角色数据
-- ----------------------------
INSERT INTO sys_role VALUES (1, '超级管理员', 'admin', 1, '1', '0', 'admin', NOW(), '', NOW(), '超级管理员', '0');
INSERT INTO sys_role VALUES (2, '普通角色', 'common', 2, '2', '0', 'admin', NOW(), '', NOW(), '普通角色', '0');

-- ----------------------------
-- 初始化用户角色关联数据
-- ----------------------------
INSERT INTO sys_user_role VALUES (1, 1);
INSERT INTO sys_user_role VALUES (2, 2);

-- ----------------------------
-- 初始化岗位数据
-- ----------------------------
INSERT INTO sys_post VALUES (1, 'ceo', '董事长', 1, '0', 'admin', NOW(), '', NOW(), '', '0');
INSERT INTO sys_post VALUES (2, 'se', '项目经理', 2, '0', 'admin', NOW(), '', NOW(), '', '0');
INSERT INTO sys_post VALUES (3, 'hr', '人力资源', 3, '0', 'admin', NOW(), '', NOW(), '', '0');
INSERT INTO sys_post VALUES (4, 'user', '普通员工', 4, '0', 'admin', NOW(), '', NOW(), '', '0');

-- ----------------------------
-- 初始化用户岗位关联数据
-- ----------------------------
INSERT INTO sys_user_post VALUES (1, 1);
INSERT INTO sys_user_post VALUES (2, 2);

-- ----------------------------
-- 初始化菜单数据
-- ----------------------------
-- 一级菜单
INSERT INTO sys_menu VALUES (1, '系统管理', 0, 1, 'system', NULL, '', '1', '0', 'M', '0', '0', '', 'setting', 'admin', NOW(), '', NOW(), '系统管理目录');
INSERT INTO sys_menu VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NOW(), '系统监控目录');
INSERT INTO sys_menu VALUES (3, '系统工具', 0, 3, 'tool', NULL, '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', NOW(), '', NOW(), '系统工具目录');

-- 二级菜单 - 系统管理
INSERT INTO sys_menu VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW(), '', NOW(), '用户管理菜单');
INSERT INTO sys_menu VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', NOW(), '', NOW(), '角色管理菜单');
INSERT INTO sys_menu VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', NOW(), '', NOW(), '菜单管理菜单');
INSERT INTO sys_menu VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', NOW(), '', NOW(), '部门管理菜单');
INSERT INTO sys_menu VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', NOW(), '', NOW(), '岗位管理菜单');
INSERT INTO sys_menu VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', NOW(), '', NOW(), '字典管理菜单');
INSERT INTO sys_menu VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', NOW(), '', NOW(), '参数设置菜单');

-- 二级菜单 - 系统监控
INSERT INTO sys_menu VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', NOW(), '', NOW(), '在线用户菜单');
INSERT INTO sys_menu VALUES (110, '操作日志', 2, 2, 'operlog', 'monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', NOW(), '', NOW(), '操作日志菜单');
INSERT INTO sys_menu VALUES (111, '登录日志', 2, 3, 'loginlog', 'monitor/loginlog/index', '', '1', '0', 'C', '0', '0', 'monitor:loginlog:list', 'logininfor', 'admin', NOW(), '', NOW(), '登录日志菜单');

-- 二级菜单 - 系统工具
INSERT INTO sys_menu VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', NOW(), '', NOW(), '代码生成菜单');
INSERT INTO sys_menu VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', NOW(), '', NOW(), '系统接口菜单');

-- 用户管理按钮
INSERT INTO sys_menu VALUES (1000, '用户查询', 100, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1001, '用户新增', 100, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1002, '用户修改', 100, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1003, '用户删除', 100, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1004, '用户导出', 100, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1005, '用户导入', 100, 6, '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1006, '重置密码', 100, 7, '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', NOW(), '', NOW(), '');

-- 角色管理按钮
INSERT INTO sys_menu VALUES (1007, '角色查询', 101, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1008, '角色新增', 101, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1009, '角色修改', 101, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1010, '角色删除', 101, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1011, '角色导出', 101, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', NOW(), '', NOW(), '');

-- 菜单管理按钮
INSERT INTO sys_menu VALUES (1012, '菜单查询', 102, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1013, '菜单新增', 102, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1014, '菜单修改', 102, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1015, '菜单删除', 102, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 部门管理按钮
INSERT INTO sys_menu VALUES (1016, '部门查询', 103, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1017, '部门新增', 103, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1018, '部门修改', 103, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1019, '部门删除', 103, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', NOW(), '', NOW(), '');

-- 岗位管理按钮
INSERT INTO sys_menu VALUES (1020, '岗位查询', 104, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1021, '岗位新增', 104, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1022, '岗位修改', 104, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1023, '岗位删除', 104, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1024, '岗位导出', 104, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', NOW(), '', NOW(), '');

-- 字典管理按钮
INSERT INTO sys_menu VALUES (1025, '字典查询', 105, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1026, '字典新增', 105, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1027, '字典修改', 105, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1028, '字典删除', 105, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1029, '字典导出', 105, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', NOW(), '', NOW(), '');

-- 参数设置按钮
INSERT INTO sys_menu VALUES (1030, '参数查询', 106, 1, '', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1031, '参数新增', 106, 2, '', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1032, '参数修改', 106, 3, '', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1033, '参数删除', 106, 4, '', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1034, '参数导出', 106, 5, '', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', NOW(), '', NOW(), '');

-- 在线用户按钮
INSERT INTO sys_menu VALUES (1040, '在线查询', 109, 1, '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1041, '批量强退', 109, 2, '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1042, '单条强退', 109, 3, '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', NOW(), '', NOW(), '');

-- 操作日志按钮
INSERT INTO sys_menu VALUES (1043, '操作查询', 110, 1, '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1044, '操作删除', 110, 2, '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1045, '日志导出', 110, 3, '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', NOW(), '', NOW(), '');

-- 登录日志按钮
INSERT INTO sys_menu VALUES (1046, '登录查询', 111, 1, '', '', '', '1', '0', 'F', '0', '0', 'monitor:loginlog:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1047, '登录删除', 111, 2, '', '', '', '1', '0', 'F', '0', '0', 'monitor:loginlog:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1048, '日志导出', 111, 3, '', '', '', '1', '0', 'F', '0', '0', 'monitor:loginlog:export', '#', 'admin', NOW(), '', NOW(), '');

-- 代码生成按钮
INSERT INTO sys_menu VALUES (1055, '生成查询', 115, 1, '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1056, '生成修改', 115, 2, '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1057, '生成删除', 115, 3, '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1058, '导入代码', 115, 4, '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1059, '预览代码', 115, 5, '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', NOW(), '', NOW(), '');
INSERT INTO sys_menu VALUES (1060, '生成代码', 115, 6, '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', NOW(), '', NOW(), '');

-- ----------------------------
-- 初始化角色菜单关联数据（普通角色）
-- ----------------------------
INSERT INTO sys_role_menu VALUES (2, 1);
INSERT INTO sys_role_menu VALUES (2, 2);
INSERT INTO sys_role_menu VALUES (2, 100);
INSERT INTO sys_role_menu VALUES (2, 101);
INSERT INTO sys_role_menu VALUES (2, 102);
INSERT INTO sys_role_menu VALUES (2, 103);
INSERT INTO sys_role_menu VALUES (2, 104);
INSERT INTO sys_role_menu VALUES (2, 105);
INSERT INTO sys_role_menu VALUES (2, 106);
INSERT INTO sys_role_menu VALUES (2, 109);
INSERT INTO sys_role_menu VALUES (2, 110);
INSERT INTO sys_role_menu VALUES (2, 111);
INSERT INTO sys_role_menu VALUES (2, 1000);
INSERT INTO sys_role_menu VALUES (2, 1007);
INSERT INTO sys_role_menu VALUES (2, 1012);
INSERT INTO sys_role_menu VALUES (2, 1016);
INSERT INTO sys_role_menu VALUES (2, 1020);
INSERT INTO sys_role_menu VALUES (2, 1025);
INSERT INTO sys_role_menu VALUES (2, 1030);
INSERT INTO sys_role_menu VALUES (2, 1040);
INSERT INTO sys_role_menu VALUES (2, 1043);
INSERT INTO sys_role_menu VALUES (2, 1046);

-- ----------------------------
-- 初始化字典类型数据
-- ----------------------------
INSERT INTO sys_dict_type VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', NOW(), '', NOW(), '用户性别列表');
INSERT INTO sys_dict_type VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', NOW(), '', NOW(), '菜单状态列表');
INSERT INTO sys_dict_type VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', NOW(), '', NOW(), '系统开关列表');
INSERT INTO sys_dict_type VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', NOW(), '', NOW(), '任务状态列表');
INSERT INTO sys_dict_type VALUES (5, '系统是否', 'sys_yes_no', '0', 'admin', NOW(), '', NOW(), '系统是否列表');
INSERT INTO sys_dict_type VALUES (6, '通知类型', 'sys_notice_type', '0', 'admin', NOW(), '', NOW(), '通知类型列表');
INSERT INTO sys_dict_type VALUES (7, '通知状态', 'sys_notice_status', '0', 'admin', NOW(), '', NOW(), '通知状态列表');
INSERT INTO sys_dict_type VALUES (8, '操作类型', 'sys_oper_type', '0', 'admin', NOW(), '', NOW(), '操作类型列表');
INSERT INTO sys_dict_type VALUES (9, '系统状态', 'sys_common_status', '0', 'admin', NOW(), '', NOW(), '登录状态列表');

-- ----------------------------
-- 初始化字典数据
-- ----------------------------
INSERT INTO sys_dict_data VALUES (1, 1, '男', '1', 'sys_user_sex', '', '', 'Y', '0', 'admin', NOW(), '', NOW(), '性别男');
INSERT INTO sys_dict_data VALUES (2, 2, '女', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NOW(), '性别女');
INSERT INTO sys_dict_data VALUES (3, 3, '未知', '0', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NOW(), '性别未知');
INSERT INTO sys_dict_data VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', NOW(), '', NOW(), '显示菜单');
INSERT INTO sys_dict_data VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '隐藏菜单');
INSERT INTO sys_dict_data VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', NOW(), '', NOW(), '正常状态');
INSERT INTO sys_dict_data VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '停用状态');
INSERT INTO sys_dict_data VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', NOW(), '', NOW(), '正常状态');
INSERT INTO sys_dict_data VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '停用状态');
INSERT INTO sys_dict_data VALUES (10, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', NOW(), '', NOW(), '系统默认是');
INSERT INTO sys_dict_data VALUES (11, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '系统默认否');
INSERT INTO sys_dict_data VALUES (12, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', NOW(), '', NOW(), '通知');
INSERT INTO sys_dict_data VALUES (13, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', NOW(), '', NOW(), '公告');
INSERT INTO sys_dict_data VALUES (14, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', NOW(), '', NOW(), '正常状态');
INSERT INTO sys_dict_data VALUES (15, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '关闭状态');
INSERT INTO sys_dict_data VALUES (16, 1, '其他', '0', 'sys_oper_type', '', 'default', 'N', '0', 'admin', NOW(), '', NOW(), '其他操作');
INSERT INTO sys_dict_data VALUES (17, 2, '新增', '1', 'sys_oper_type', '', 'success', 'N', '0', 'admin', NOW(), '', NOW(), '新增操作');
INSERT INTO sys_dict_data VALUES (18, 3, '修改', '2', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NOW(), '修改操作');
INSERT INTO sys_dict_data VALUES (19, 4, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '删除操作');
INSERT INTO sys_dict_data VALUES (20, 5, '授权', '4', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NOW(), '授权操作');
INSERT INTO sys_dict_data VALUES (21, 6, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NOW(), '导出操作');
INSERT INTO sys_dict_data VALUES (22, 7, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NOW(), '导入操作');
INSERT INTO sys_dict_data VALUES (23, 8, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '强退操作');
INSERT INTO sys_dict_data VALUES (24, 9, '清空', '8', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '清空操作');
INSERT INTO sys_dict_data VALUES (25, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NOW(), '正常状态');
INSERT INTO sys_dict_data VALUES (26, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NOW(), '停用状态');

-- ----------------------------
-- 初始化系统配置数据
-- ----------------------------
INSERT INTO sys_config VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', NOW(), '', NOW(), '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO sys_config VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', NOW(), '', NOW(), '初始化密码 123456');
INSERT INTO sys_config VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', NOW(), '', NOW(), '深色主题theme-dark，浅色主题theme-light');
INSERT INTO sys_config VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', NOW(), '', NOW(), '是否开启验证码功能（true开启，false关闭）');
INSERT INTO sys_config VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', NOW(), '', NOW(), '是否开启注册用户功能（true开启，false关闭）');
