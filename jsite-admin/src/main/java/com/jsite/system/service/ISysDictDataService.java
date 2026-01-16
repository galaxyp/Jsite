package com.jsite.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.system.domain.SysDictData;

import java.util.List;

/**
 * 字典 业务层
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /**
     * 分页查询字典数据列表
     */
    TableDataInfo<SysDictData> selectPageDictDataList(SysDictData dictData);

    /**
     * 根据条件查询字典数据
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     */
    String selectDictLabel(String dictType, String dictValue);

    /**
     * 根据字典数据ID查询信息
     */
    SysDictData selectDictDataById(Long dictCode);

    /**
     * 批量删除字典数据信息
     */
    void deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增保存字典数据信息
     */
    int insertDictData(SysDictData dictData);

    /**
     * 修改保存字典数据信息
     */
    int updateDictData(SysDictData dictData);
}
