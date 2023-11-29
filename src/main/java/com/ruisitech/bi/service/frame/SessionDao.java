/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class SessionDao extends CachingSessionDAO {

	private String prefix = "session-";

	@Resource
	private RedisTemplate<String, Session> redisTemplate;

	@Override
	protected void doUpdate(Session session) {
		String k = prefix + session.getId();
		redisTemplate.opsForValue().set(k, session, 30, TimeUnit.MINUTES);
	}

	@Override
	protected void doDelete(Session session) {
		String k = prefix + session.getId();
		redisTemplate.delete(k);
	}

	public void delSession(Session session) {
		this.delete(session);
	}

	@Override
	protected Serializable doCreate(Session session) {
		Serializable sessionId = this.generateSessionId(session);
		assignSessionId(session, sessionId);
		String k = prefix + sessionId;
		redisTemplate.opsForValue().set(k, session, 30, TimeUnit.MINUTES);
		return sessionId;
	}

	@Override
	protected Session doReadSession(Serializable sessionId) {
		String k = prefix + sessionId;
		try {
			return (Session)redisTemplate.opsForValue().get(k);
		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
