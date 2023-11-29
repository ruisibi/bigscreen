/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.bireport;

import com.rsbi.ispire.dc.grid.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 数据中心缓存对象
 * @author zxd
 * @Date 2019年8月12日
 */
@Service
public class DataCenterCacheService implements CacheService<String, List<Map<String, Object>>> {

	private String prefix = "dc-";

	@Resource
	private RedisTemplate<String, List<Map<String, Object>>> redisTemplate;

	@Override
	public List<Map<String, Object>> get(String k) {
		return redisTemplate.opsForValue().get(prefix+k);
	}

	@Override
	public void put(String k, List<Map<String, Object>> v, Integer times) {
		redisTemplate.opsForValue().set(prefix+k, v, times, TimeUnit.SECONDS);
	}

}
