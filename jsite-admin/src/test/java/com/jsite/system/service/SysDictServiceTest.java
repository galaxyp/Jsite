package com.jsite.system.service;

import com.jsite.system.domain.SysDictData;
import com.jsite.system.domain.SysDictType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字典服务测试
 */
@SpringBootTest
@Transactional
class SysDictServiceTest {

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Autowired
    private ISysDictDataService dictDataService;

    @Test
    void testSelectDictTypeList() {
        SysDictType query = new SysDictType();
        List<SysDictType> list = dictTypeService.selectDictTypeList(query);
        assertNotNull(list);
    }

    @Test
    void testSelectDictTypeByType() {
        // 查询用户性别字典类型
        SysDictType dictType = dictTypeService.selectDictTypeByType("sys_user_sex");
        assertNotNull(dictType);
    }

    @Test
    void testSelectDictDataList() {
        // 查询字典数据列表
        SysDictData query = new SysDictData();
        query.setDictType("sys_user_sex");
        List<SysDictData> list = dictDataService.selectDictDataList(query);
        assertNotNull(list);
    }

    @Test
    void testCheckDictTypeUnique() {
        // checkDictTypeUnique 返回 true 表示"类型名可用（唯一）"，false 表示"已被占用（不唯一）"
        // 场景1：已存在的类型名 sys_user_sex，不传 dictId（视为新增），应返回 false（已被占用）
        SysDictType existing = new SysDictType();
        existing.setDictType("sys_user_sex");
        assertFalse(dictTypeService.checkDictTypeUnique(existing), "已存在的字典类型应不唯一");

        // 场景2：不存在的类型名，应返回 true（可用）
        SysDictType newType = new SysDictType();
        newType.setDictType("sys_not_exist_type_xyz");
        assertTrue(dictTypeService.checkDictTypeUnique(newType), "不存在的字典类型应唯一");
    }
}
