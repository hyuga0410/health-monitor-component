package com.hyuga.service.agent.monitor.cache;


import kfang.infra.common.cache.cachekey.CacheKeyType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 常量缓存键
 *
 * @author hyuga
 * @since 2018-10-26
 */
@NoArgsConstructor
@AllArgsConstructor
public enum CacheKey implements CacheKeyType {

    /**
     *
     */
    SERVICE_WARNING(CacheBaseType.NOTIFY),

    ;

    private CacheBaseType baseType;

    @Override
    public String getBaseType() {
        return this.baseType.name();
    }

    @Override
    public String getCacheKey() {
        return this.name();
    }

}