package com.yurie.miaosha.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yurie.miaosha.service.CacheService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {
    private Cache<String, Object> commonCache;

    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
                // 设置初始容量
                .initialCapacity(10)
                // 设置最大容量，当超过最大容量时，将根据LRU策略移除缓存。
                .maximumSize(100)
                // 设置过期时间
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key, value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
