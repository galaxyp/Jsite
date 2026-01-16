package com.jsite.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsite.common.constant.CacheConstants;
import com.jsite.common.core.page.PageQuery;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.system.domain.SysDictData;
import com.jsite.system.mapper.SysDictDataMapper;
import com.jsite.system.service.ISysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 字典 业务层处理
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    private final SysDictDataMapper dictDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public TableDataInfo<SysDictData> selectPageDictDataList(SysDictData dictData) {
        Page<SysDictData> page = new Page<>(PageQuery.getPageNum(), PageQuery.getPageSize());
        Page<SysDictData> result = dictDataMapper.selectDictDataList(page, dictData);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        Page<SysDictData> page = new Page<>(1, Integer.MAX_VALUE);
        return dictDataMapper.selectDictDataList(page, dictData).getRecords();
    }

    @Override
    public String selectDictLabel(String dictType, String dictValue) {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    @Override
    public SysDictData selectDictDataById(Long dictCode) {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {
        for (Long dictCode : dictCodes) {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteById(dictCode);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            redisTemplate.opsForValue().set(getCacheKey(data.getDictType()), dictDatas);
        }
    }

    @Override
    public int insertDictData(SysDictData dictData) {
        int row = dictDataMapper.insert(dictData);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictData.getDictType());
            redisTemplate.opsForValue().set(getCacheKey(dictData.getDictType()), dictDatas);
        }
        return row;
    }

    @Override
    public int updateDictData(SysDictData dictData) {
        int row = dictDataMapper.updateById(dictData);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictData.getDictType());
            redisTemplate.opsForValue().set(getCacheKey(dictData.getDictType()), dictDatas);
        }
        return row;
    }

    /**
     * 设置cache key
     */
    private String getCacheKey(String dictType) {
        return CacheConstants.SYS_DICT_KEY + dictType;
    }
}
