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
        // 测试字典类型唯一性检查
        SysDictType dictType = new SysDictType();
        dictType.setDictType("sys_user_sex");
        boolean exists = dictTypeService.checkDictTypeUnique(dictType);
        assertTrue(exists);
    }
}
