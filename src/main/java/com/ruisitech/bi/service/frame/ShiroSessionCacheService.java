package com.ruisitech.bi.service.frame;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Shiro sessionCache
 * @author hq
 */
public class ShiroSessionCacheService implements Cache<Object, Object> {

	private RedisTemplate<Object, Object> redisTemplate;

	private String prefix = "cache-";

	public ShiroSessionCacheService(RedisTemplate<Object, Object> redis){
		this.redisTemplate = redis;
	}

	@Override
	public Object get(Object key) throws CacheException {
		return redisTemplate.opsForValue().get(prefix + key);
	}

	@Override
	public Object put(Object key, Object value) throws CacheException {
		redisTemplate.opsForValue().set(prefix + key, value, 30, TimeUnit.MINUTES);
		return value;
	}

	@Override
	public Object remove(Object key) throws CacheException {
		Object ret = this.get(key);
		redisTemplate.delete(prefix + key);
		return ret;
	}

	@Override
	public void clear() throws CacheException {
		Set<Object> keys = redisTemplate.keys(prefix + "*");
		redisTemplate.delete(keys);
	}

	@Override
	public int size() {
		Set<Object> keys = redisTemplate.keys(prefix + "*");
		return keys.size();
	}

	@Override
	public Set<Object> keys() {
		return redisTemplate.keys(prefix + "*");
	}

	@Override
	public Collection<Object> values() {
		Set<Object> keys = redisTemplate.keys(prefix + "*");
		return redisTemplate.opsForValue().multiGet(keys);
	}

}
