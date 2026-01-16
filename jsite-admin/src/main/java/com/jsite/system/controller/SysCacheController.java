package com.jsite.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollUtil;
import com.jsite.common.core.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存监控
 */
@Tag(name = "缓存监控")
@RestController
@RequestMapping("/monitor/cache")
@RequiredArgsConstructor
public class SysCacheController {

    private final RedisTemplate<String, Object> redisTemplate;

    private final static List<Map<String, String>> CACHES = new ArrayList<>();

    static {
        CACHES.add(createCache("login_tokens:", "用户登录信息"));
        CACHES.add(createCache("sys_config:", "配置信息"));
        CACHES.add(createCache("sys_dict:", "数据字典"));
        CACHES.add(createCache("captcha_codes:", "验证码"));
        CACHES.add(createCache("repeat_submit:", "防重提交"));
        CACHES.add(createCache("rate_limit:", "限流处理"));
    }

    private static Map<String, String> createCache(String name, String remark) {
        Map<String, String> cache = new HashMap<>();
        cache.put("cacheName", name);
        cache.put("remark", remark);
        return cache;
    }

    /**
     * 获取缓存监控信息
     */
    @Operation(summary = "获取缓存监控信息")
    @SaCheckPermission("monitor:cache:list")
    @GetMapping
    public R<Map<String, Object>> getInfo() {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::info);
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection ->
                connection.serverCommands().info("commandstats"));
        Long dbSize = (Long) redisTemplate.execute((RedisCallback<Object>) RedisServerCommands::dbSize);

        Map<String, Object> result = new HashMap<>();
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        if (commandStats != null) {
            commandStats.stringPropertyNames().forEach(key -> {
                Map<String, String> data = new HashMap<>();
                String property = commandStats.getProperty(key);
                data.put("name", key.replace("cmdstat_", ""));
                data.put("value", property.substring(property.indexOf("calls=") + 6, property.indexOf(",usec")));
                pieList.add(data);
            });
        }
        result.put("commandStats", pieList);
        return R.ok(result);
    }

    /**
     * 获取缓存名称列表
     */
    @Operation(summary = "获取缓存名称列表")
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/getNames")
    public R<List<Map<String, String>>> getNames() {
        return R.ok(CACHES);
    }

    /**
     * 获取缓存键名列表
     */
    @Operation(summary = "获取缓存键名列表")
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/getKeys/{cacheName}")
    public R<Set<String>> getKeys(@PathVariable String cacheName) {
        Set<String> keys = redisTemplate.keys(cacheName + "*");
        return R.ok(keys);
    }

    /**
     * 获取缓存内容
     */
    @Operation(summary = "获取缓存内容")
    @SaCheckPermission("monitor:cache:list")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    public R<Map<String, Object>> getValue(@PathVariable String cacheName, @PathVariable String cacheKey) {
        String key = cacheName + cacheKey;
        Object value = redisTemplate.opsForValue().get(key);
        Map<String, Object> result = new HashMap<>();
        result.put("cacheName", cacheName);
        result.put("cacheKey", cacheKey);
        result.put("cacheValue", value != null ? value.toString() : "");
        return R.ok(result);
    }

    /**
     * 清理缓存名称
     */
    @Operation(summary = "清理缓存名称")
    @SaCheckPermission("monitor:cache:list")
    @DeleteMapping("/clearCacheName/{cacheName}")
    public R<Void> clearCacheName(@PathVariable String cacheName) {
        Set<String> keys = redisTemplate.keys(cacheName + "*");
        if (CollUtil.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        return R.ok();
    }

    /**
     * 清理缓存键名
     */
    @Operation(summary = "清理缓存键名")
    @SaCheckPermission("monitor:cache:list")
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    public R<Void> clearCacheKey(@PathVariable String cacheKey) {
        redisTemplate.delete(cacheKey);
        return R.ok();
    }

    /**
     * 清理全部缓存
     */
    @Operation(summary = "清理全部缓存")
    @SaCheckPermission("monitor:cache:list")
    @DeleteMapping("/clearCacheAll")
    public R<Void> clearCacheAll() {
        Set<String> keys = redisTemplate.keys("*");
        if (CollUtil.isNotEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        return R.ok();
    }
}
