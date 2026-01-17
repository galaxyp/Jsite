-- ----------------------------
-- JSite 快速开发平台数据库初始化脚本 v2
-- 数据库：MySQL 8.0+
-- 注意：此版本字段名与代码Mapper保持一致
-- ----------------------------

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id         BIGINT          DEFAULT NULL COMMENT '部门ID',
    user_name       VARCHAR(30)     NOT NULL COMMENT '登录账号',
    nick_name       VARCHAR(30)     NOT NULL COMMENT '用户昵称',
    user_type       VARCHAR(10)     DEFAULT 'sys' COMMENT '用户类型（sys系统用户）',
    email           VARCHAR(50)     DEFAULT '' COMMENT '用户邮箱',
    phonenumber     VARCHAR(11)     DEFAULT '' COMMENT '手机号码',
    sex             CHAR(1)         DEFAULT '0' COMMENT '性别（0未知 1男 2女）',
    avatar          VARCHAR(200)    DEFAULT '' COMMENT '头像路径',
    password        VARCHAR(100)    DEFAULT '' COMMENT '密码',
    status          CHAR(1)         DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    login_ip        VARCHAR(128)    DEFAULT '' COMMENT '最后登录IP',
    login_date      DATETIME        DEFAULT NULL COMMENT '最后登录时间',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_user_name (user_name)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户表';

-- ----------------------------
-- 2. 部门表
-- ----------------------------
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    dept_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '部门ID',
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
    PRIMARY KEY (dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=200 COMMENT='部门表';

-- ----------------------------
-- 3. 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name       VARCHAR(30)     NOT NULL COMMENT '角色名称',
    role_key        VARCHAR(100)    NOT NULL COMMENT '角色权限字符串',
    role_sort       INT             NOT NULL COMMENT '显示顺序',
    data_scope      CHAR(1)         DEFAULT '1' COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    menu_check_strictly     TINYINT(1)  DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    dept_check_strictly     TINYINT(1)  DEFAULT 1 COMMENT '部门树选择项是否关联显示',
    status          CHAR(1)         DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    del_flag        CHAR(1)         DEFAULT '0' COMMENT '删除标志（0正常 1删除）',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色表';

-- ----------------------------
-- 4. 菜单权限表
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    menu_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
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
    PRIMARY KEY (menu_id)
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
    post_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    post_code       VARCHAR(64)     NOT NULL COMMENT '岗位编码',
    post_name       VARCHAR(50)     NOT NULL COMMENT '岗位名称',
    post_sort       INT             NOT NULL COMMENT '显示顺序',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (post_id)
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
    dict_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    dict_name       VARCHAR(100)    DEFAULT '' COMMENT '字典名称',
    dict_type       VARCHAR(100)    DEFAULT '' COMMENT '字典类型',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (dict_id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典类型表';

-- ----------------------------
-- 11. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    dict_code       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    dict_sort       INT             DEFAULT 0 COMMENT '字典排序',
    dict_label      VARCHAR(100)    DEFAULT '' COMMENT '字典标签',
    dict_value      VARCHAR(100)    DEFAULT '' COMMENT '字典键值',
    dict_type       VARCHAR(100)    DEFAULT '' COMMENT '字典类型',
    css_class       VARCHAR(100)    DEFAULT '' COMMENT '样式属性',
    list_class      VARCHAR(100)    DEFAULT '' COMMENT '表格回显样式',
    is_default      CHAR(1)         DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    status          CHAR(1)         DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (dict_code)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典数据表';

-- ----------------------------
-- 12. 参数配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    config_id       BIGINT          NOT NULL AUTO_INCREMENT COMMENT '参数ID',
    config_name     VARCHAR(100)    DEFAULT '' COMMENT '参数名称',
    config_key      VARCHAR(100)    DEFAULT '' COMMENT '参数键名',
    config_value    VARCHAR(500)    DEFAULT '' COMMENT '参数键值',
    config_type     CHAR(1)         DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    create_by       VARCHAR(64)     DEFAULT '' COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)     DEFAULT '' COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)    DEFAULT '' COMMENT '备注',
    PRIMARY KEY (config_id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='参数配置表';

-- ----------------------------
-- 13. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    oper_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    title           VARCHAR(50)     DEFAULT '' COMMENT '模块标题',
    business_type   INT             DEFAULT 0 COMMENT '业务类型',
    method          VARCHAR(100)    DEFAULT '' COMMENT '方法名称',
    request_method  VARCHAR(10)     DEFAULT '' COMMENT '请求方式',
    operator_type   INT             DEFAULT 0 COMMENT '操作类别',
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
    cost_time       BIGINT          DEFAULT 0 COMMENT '消耗时间',
    PRIMARY KEY (oper_id),
    KEY idx_oper_time (oper_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='操作日志表';

-- ----------------------------
-- 14. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
    info_id         BIGINT          NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    user_name       VARCHAR(50)     DEFAULT '' COMMENT '用户账号',
    ipaddr          VARCHAR(128)    DEFAULT '' COMMENT '登录IP',
    login_location  VARCHAR(255)    DEFAULT '' COMMENT '登录地点',
    browser         VARCHAR(50)     DEFAULT '' COMMENT '浏览器类型',
    os              VARCHAR(50)     DEFAULT '' COMMENT '操作系统',
    status          CHAR(1)         DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    msg             VARCHAR(255)    DEFAULT '' COMMENT '提示消息',
    login_time      DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
    PRIMARY KEY (info_id),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='登录日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 初始化数据
-- ----------------------------

-- 部门数据
INSERT INTO sys_dept VALUES(100, 0, '0', 'JSite科技', 0, '管理员', '15888888888', 'admin@jsite.com', '0', 'admin', NOW(), '', NULL, '0');
INSERT INTO sys_dept VALUES(101, 100, '0,100', '深圳总公司', 1, '管理员', '15888888888', 'shenzhen@jsite.com', '0', 'admin', NOW(), '', NULL, '0');
INSERT INTO sys_dept VALUES(102, 100, '0,100', '长沙分公司', 2, '管理员', '15888888888', 'changsha@jsite.com', '0', 'admin', NOW(), '', NULL, '0');

-- 用户数据 密码: admin123
INSERT INTO sys_user VALUES(1, 103, 'admin', '超级管理员', 'sys', 'admin@jsite.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '管理员', '0');
INSERT INTO sys_user VALUES(2, 105, 'jsite', 'JSite', 'sys', 'jsite@jsite.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '127.0.0.1', NOW(), 'admin', NOW(), '', NULL, '测试员', '0');

-- 角色数据
INSERT INTO sys_role VALUES(1, '超级管理员', 'admin', 1, '1', 1, 1, '0', 'admin', NOW(), '', NULL, '超级管理员', '0');
INSERT INTO sys_role VALUES(2, '普通角色', 'common', 2, '2', 1, 1, '0', 'admin', NOW(), '', NULL, '普通角色', '0');

-- 菜单数据
INSERT INTO sys_menu VALUES(1, '系统管理', 0, 1, 'system', NULL, '', '1', '0', 'M', '0', '0', '', 'setting', 'admin', NOW(), '', NULL, '系统管理目录');
INSERT INTO sys_menu VALUES(2, '系统监控', 0, 2, 'monitor', NULL, '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, '系统监控目录');
INSERT INTO sys_menu VALUES(3, '系统工具', 0, 3, 'tool', NULL, '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', NOW(), '', NULL, '系统工具目录');

-- 系统管理子菜单
INSERT INTO sys_menu VALUES(100, '用户管理', 1, 1, 'user', 'system/user/index', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', NOW(), '', NULL, '用户管理菜单');
INSERT INTO sys_menu VALUES(101, '角色管理', 1, 2, 'role', 'system/role/index', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', NOW(), '', NULL, '角色管理菜单');
INSERT INTO sys_menu VALUES(102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', NOW(), '', NULL, '菜单管理菜单');
INSERT INTO sys_menu VALUES(103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', NOW(), '', NULL, '部门管理菜单');
INSERT INTO sys_menu VALUES(104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', NOW(), '', NULL, '岗位管理菜单');
INSERT INTO sys_menu VALUES(105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', NOW(), '', NULL, '字典管理菜单');
INSERT INTO sys_menu VALUES(106, '参数设置', 1, 7, 'config', 'system/config/index', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', NOW(), '', NULL, '参数设置菜单');

-- 系统监控子菜单
INSERT INTO sys_menu VALUES(109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', NOW(), '', NULL, '在线用户菜单');
INSERT INTO sys_menu VALUES(110, '操作日志', 2, 2, 'operlog', 'monitor/operlog/index', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', NOW(), '', NULL, '操作日志菜单');
INSERT INTO sys_menu VALUES(111, '登录日志', 2, 3, 'logininfor', 'monitor/logininfor/index', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', NOW(), '', NULL, '登录日志菜单');
INSERT INTO sys_menu VALUES(112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', NOW(), '', NULL, '服务监控菜单');
INSERT INTO sys_menu VALUES(113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', NOW(), '', NULL, '缓存监控菜单');

-- 系统工具子菜单
INSERT INTO sys_menu VALUES(115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', NOW(), '', NULL, '代码生成菜单');

-- 用户角色关联
INSERT INTO sys_user_role VALUES(1, 1);
INSERT INTO sys_user_role VALUES(2, 2);

-- 角色菜单关联 - 管理员拥有所有菜单
INSERT INTO sys_role_menu VALUES(1, 1);
INSERT INTO sys_role_menu VALUES(1, 2);
INSERT INTO sys_role_menu VALUES(1, 3);
INSERT INTO sys_role_menu VALUES(1, 100);
INSERT INTO sys_role_menu VALUES(1, 101);
INSERT INTO sys_role_menu VALUES(1, 102);
INSERT INTO sys_role_menu VALUES(1, 103);
INSERT INTO sys_role_menu VALUES(1, 104);
INSERT INTO sys_role_menu VALUES(1, 105);
INSERT INTO sys_role_menu VALUES(1, 106);
INSERT INTO sys_role_menu VALUES(1, 109);
INSERT INTO sys_role_menu VALUES(1, 110);
INSERT INTO sys_role_menu VALUES(1, 111);
INSERT INTO sys_role_menu VALUES(1, 112);
INSERT INTO sys_role_menu VALUES(1, 113);
INSERT INTO sys_role_menu VALUES(1, 115);

-- 岗位数据
INSERT INTO sys_post VALUES(1, 'ceo', '董事长', 1, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_post VALUES(2, 'se', '项目经理', 2, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_post VALUES(3, 'hr', '人力资源', 3, '0', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_post VALUES(4, 'user', '普通员工', 4, '0', 'admin', NOW(), '', NULL, '');

-- 用户岗位关联
INSERT INTO sys_user_post VALUES(1, 1);
INSERT INTO sys_user_post VALUES(2, 2);

-- 字典类型数据
INSERT INTO sys_dict_type VALUES(1, '用户性别', 'sys_user_sex', '0', 'admin', NOW(), '', NULL, '用户性别列表');
INSERT INTO sys_dict_type VALUES(2, '菜单状态', 'sys_show_hide', '0', 'admin', NOW(), '', NULL, '菜单状态列表');
INSERT INTO sys_dict_type VALUES(3, '系统开关', 'sys_normal_disable', '0', 'admin', NOW(), '', NULL, '系统开关列表');
INSERT INTO sys_dict_type VALUES(4, '系统是否', 'sys_yes_no', '0', 'admin', NOW(), '', NULL, '系统是否列表');

-- 字典数据
INSERT INTO sys_dict_data VALUES(1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', NOW(), '', NULL, '性别男');
INSERT INTO sys_dict_data VALUES(2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '性别女');
INSERT INTO sys_dict_data VALUES(3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', NOW(), '', NULL, '性别未知');
INSERT INTO sys_dict_data VALUES(4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '显示菜单');
INSERT INTO sys_dict_data VALUES(5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '隐藏菜单');
INSERT INTO sys_dict_data VALUES(6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '正常状态');
INSERT INTO sys_dict_data VALUES(7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '停用状态');
INSERT INTO sys_dict_data VALUES(8, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '系统默认是');
INSERT INTO sys_dict_data VALUES(9, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '系统默认否');

-- 参数配置数据
INSERT INTO sys_config VALUES(1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', NOW(), '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO sys_config VALUES(2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', NOW(), '', NULL, '初始化密码 123456');
INSERT INTO sys_config VALUES(3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', NOW(), '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO sys_config VALUES(4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', NOW(), '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO sys_config VALUES(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', NOW(), '', NULL, '是否开启注册用户功能（true开启，false关闭）');
