/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * shiro cache manager
 * @author hq
 *
 */
public class CustomCacheManager implements CacheManager {

	@Resource
	private RedisTemplate<Object, Object> redisTemplate;

	public Cache<Object, Object> getCache(String name) throws CacheException {
		return new ShiroSessionCacheService(redisTemplate);
	}
}
