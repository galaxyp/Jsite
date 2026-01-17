package com.jsite.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsite.common.constant.CacheConstants;
import com.jsite.common.core.page.PageQuery;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.exception.ServiceException;
import com.jsite.system.domain.SysDictData;
import com.jsite.system.domain.SysDictType;
import com.jsite.system.mapper.SysDictDataMapper;
import com.jsite.system.mapper.SysDictTypeMapper;
import com.jsite.system.service.ISysDictTypeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {
        try {
            loadingDictCache();
        } catch (Exception e) {
            log.warn("初始化字典缓存失败，Redis可能未启动: {}", e.getMessage());
        }
    }

    @Override
    public TableDataInfo<SysDictType> selectPageDictTypeList(SysDictType dictType) {
        Page<SysDictType> page = new Page<>(PageQuery.getPageNum(), PageQuery.getPageSize());
        IPage<SysDictType> result = dictTypeMapper.selectDictTypeList(page, dictType);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        Page<SysDictType> page = new Page<>(1, Integer.MAX_VALUE);
        return dictTypeMapper.selectDictTypeList(page, dictType).getRecords();
    }

    @Override
    public List<SysDictType> selectDictTypeAll() {
        return dictTypeMapper.selectDictTypeAll();
    }

    @Override
    public SysDictType selectDictTypeById(Long dictId) {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%1$s已分配，不能删除", dictType.getDictName()));
            }
            dictTypeMapper.deleteById(dictId);
            redisTemplate.delete(getCacheKey(dictType.getDictType()));
        }
    }

    @Override
    public void loadingDictCache() {
        List<SysDictType> dictTypeList = dictTypeMapper.selectDictTypeAll();
        for (SysDictType dictType : dictTypeList) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            redisTemplate.opsForValue().set(getCacheKey(dictType.getDictType()), dictDatas);
        }
    }

    @Override
    public void clearDictCache() {
        List<SysDictType> dictTypeList = dictTypeMapper.selectDictTypeAll();
        for (SysDictType dictType : dictTypeList) {
            redisTemplate.delete(getCacheKey(dictType.getDictType()));
        }
    }

    @Override
    public void resetDictCache() {
        clearDictCache();
        loadingDictCache();
    }

    @Override
    public int insertDictType(SysDictType dictType) {
        int row = dictTypeMapper.insert(dictType);
        if (row > 0) {
            redisTemplate.opsForValue().set(getCacheKey(dictType.getDictType()), List.of());
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDictType(SysDictType dictType) {
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dictType.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        int row = dictTypeMapper.updateById(dictType);
        if (row > 0) {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            redisTemplate.delete(getCacheKey(oldDict.getDictType()));
            redisTemplate.opsForValue().set(getCacheKey(dictType.getDictType()), dictDatas);
        }
        return row;
    }

    @Override
    public boolean checkDictTypeUnique(SysDictType dictType) {
        Long dictId = ObjectUtil.isNull(dictType.getDictId()) ? -1L : dictType.getDictId();
        SysDictType info = dictTypeMapper.checkDictTypeUnique(dictType.getDictType());
        if (ObjectUtil.isNotNull(info) && info.getDictId().longValue() != dictId.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 设置cache key
     */
    private String getCacheKey(String dictType) {
        return CacheConstants.SYS_DICT_KEY + dictType;
    }
}
