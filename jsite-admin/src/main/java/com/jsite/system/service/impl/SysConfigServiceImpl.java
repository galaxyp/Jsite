package com.jsite.system.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsite.common.constant.CacheConstants;
import com.jsite.common.core.page.PageQuery;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.exception.ServiceException;
import com.jsite.system.domain.SysConfig;
import com.jsite.system.mapper.SysConfigMapper;
import com.jsite.system.service.ISysConfigService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 参数配置 服务层实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    private final SysConfigMapper configMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {
        try {
            loadingConfigCache();
        } catch (Exception e) {
            log.warn("初始化配置缓存失败，Redis可能未启动: {}", e.getMessage());
        }
    }

    @Override
    public TableDataInfo<SysConfig> selectPageConfigList(SysConfig config) {
        Page<SysConfig> page = new Page<>(PageQuery.getPageNum(), PageQuery.getPageSize());
        IPage<SysConfig> result = configMapper.selectConfigList(page, config);
        return TableDataInfo.build(result);
    }

    @Override
    public SysConfig selectConfigById(Long configId) {
        return configMapper.selectConfigById(configId);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        String configValue = Convert.toStr(redisTemplate.opsForValue().get(getCacheKey(configKey)));
        if (StrUtil.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig config = configMapper.selectConfigByKey(configKey);
        if (ObjectUtil.isNotNull(config)) {
            redisTemplate.opsForValue().set(getCacheKey(configKey), config.getConfigValue());
            return config.getConfigValue();
        }
        return StrUtil.EMPTY;
    }

    @Override
    public boolean selectCaptchaEnabled() {
        // 暂时关闭验证码
        return false;
    }

    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {
        Page<SysConfig> page = new Page<>(1, Integer.MAX_VALUE);
        return configMapper.selectConfigList(page, config).getRecords();
    }

    @Override
    public int insertConfig(SysConfig config) {
        int row = configMapper.insert(config);
        if (row > 0) {
            redisTemplate.opsForValue().set(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public int updateConfig(SysConfig config) {
        SysConfig temp = configMapper.selectConfigById(config.getConfigId());
        if (!StrUtil.equals(temp.getConfigKey(), config.getConfigKey())) {
            redisTemplate.delete(getCacheKey(temp.getConfigKey()));
        }
        int row = configMapper.updateById(config);
        if (row > 0) {
            redisTemplate.opsForValue().set(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public void deleteConfigByIds(Long[] configIds) {
        for (Long configId : configIds) {
            SysConfig config = selectConfigById(configId);
            if (StrUtil.equals("Y", config.getConfigType())) {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteById(configId);
            redisTemplate.delete(getCacheKey(config.getConfigKey()));
        }
    }

    @Override
    public void loadingConfigCache() {
        List<SysConfig> configList = selectConfigList(new SysConfig());
        for (SysConfig config : configList) {
            redisTemplate.opsForValue().set(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    @Override
    public void clearConfigCache() {
        List<SysConfig> configList = selectConfigList(new SysConfig());
        for (SysConfig config : configList) {
            redisTemplate.delete(getCacheKey(config.getConfigKey()));
        }
    }

    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {
        Long configId = ObjectUtil.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (ObjectUtil.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 设置cache key
     */
    private String getCacheKey(String configKey) {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
