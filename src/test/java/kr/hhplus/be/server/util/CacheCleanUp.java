package kr.hhplus.be.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CacheCleanUp {

    private final CacheManager redisCacheManager;
    private final CacheManager caffeineCacheManager;

    @Autowired
    public CacheCleanUp(
            @Autowired @org.springframework.beans.factory.annotation.Qualifier("redisCacheManager") CacheManager redisCacheManager,
            @Autowired @org.springframework.beans.factory.annotation.Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager) {
        this.redisCacheManager = redisCacheManager;
        this.caffeineCacheManager = caffeineCacheManager;
    }

    /**
     * 🔹 특정 캐시 삭제 (Redis & Caffeine)
     */
    public void clearCache(String cacheName) {
        // Redis 캐시 삭제
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            Objects.requireNonNull(redisCache).clear();
        }

        // Caffeine 캐시 삭제
        Cache caffeineCache = caffeineCacheManager.getCache(cacheName);
        if (caffeineCache != null) {
            Objects.requireNonNull(caffeineCache).clear();
        }
    }

    /**
     * 🔹 모든 캐시 삭제 (Redis & Caffeine)
     */
    public void clearAllCaches() {
        // Redis 캐시 삭제
        redisCacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = redisCacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });

        // Caffeine 캐시 삭제
        caffeineCacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = caffeineCacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }
}