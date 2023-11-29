/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.auth;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.mapper.frame.UserMapper;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.bi.util.TreeInterface;
import com.ruisitech.bi.util.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限管理 - 用户管理模块
 * @author gdp
 *
 */
@Service
public class AuthUserService {

	@Resource
	private UserMapper userMapper;

	@Resource(name = "sysDaoHelper")
	private DaoHelper daoHelper;

	@Autowired
	private LoggerService loggerService;

	public List<User> listUsers(String keyword){
		return userMapper.listUsers(keyword);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteUser(Integer userId) {
		User u = this.getUserById(userId);
		//删除用户
		daoHelper.execute("delete from sc_login_user where user_id = " + userId);
		//删除用户菜单关系
		daoHelper.execute("delete from user_menu_rela where user_id = " + userId);
		//删除用户角色关系
		daoHelper.execute("delete from role_user_rela where user_id = " + userId);

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", u.getUserId());
		pms.put("name", u.getLoginName());
		pms.put("operType", 3);
		pms.put("oper", "删除用户" + u.getLoginName());
		pms.put("bsType", 10);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public String saveUser(User u) {
		int cnt = userMapper.userExist(u.getStaffId());
		if(cnt > 0){
			return "账号已经存在。";
		}
		u.setUserId(userMapper.maxUserId());
		u.setPassword(RSBIUtils.getMD5(u.getPassword().getBytes()));
		userMapper.insertuser(u);

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", u.getUserId());
		pms.put("name", u.getLoginName());
		pms.put("operType", 1);
		pms.put("oper", "创建用户" + u.getLoginName());
		pms.put("bsType", 10);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
		return null;
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateUser(User u) {
		userMapper.updateuser(u);
		if(u.getPassword() != null && u.getPassword().length() > 0) {
			//修改密码
			u.setPassword(RSBIUtils.getMD5(u.getPassword().getBytes()));
			userMapper.modPsd(u);
		}
		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", u.getUserId());
		pms.put("name", u.getLoginName());
		pms.put("operType", 2);
		pms.put("oper", "修改用户" + u.getLoginName());
		pms.put("bsType", 10);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	public User getUserById(Integer userId) {
		return userMapper.getUserById(userId);
	}

	public Map<String, Object> listUserMenus(Integer userId){
		List<Map<String, Object>> ls = userMapper.listUserMenus(userId);
		TreeService ser = new TreeService();
		List<Map<String, Object>> ret = ser.createTreeData(ls, new TreeInterface(){

			@Override
			public void processMap(Map<String, Object> m) {
				Map<String, Object> state = new HashMap<String, Object>();
				String chk2 = m.get("id2") == null ? "" : m.get("id2").toString();
				if(chk2 == null || chk2.length() == 0){
					//id3为用户所拥有的菜单，需要判断是否checked
					String chk3 = m.get("id3") == null ? "" : m.get("id3").toString();
					if(chk3 == null || chk3.length() == 0){
						state.put("selected", false);
					}else{
						state.put("selected", true);
					}
					state.put("disabled", false);
				}else{
					state.put("disabled", true);
					state.put("selected", true);
				}
				m.put("state", state);
				//设置属性
				Map<String, Object> attributes = new HashMap<String, Object>();
				m.put("li_attr", attributes);
				attributes.put("id2", m.get("id2"));
				attributes.put("id3", m.get("id3"));
				attributes.put("disabled", m.get("disabled"));
			}

			@Override
			public void processEnd(Map<String, Object> m, boolean hasChild) {
				String chk3 = m.get("id3") == null ? "" : m.get("id3").toString();
				if(hasChild && chk3 != null && chk3.length() > 0){
					m.remove("checked");
				}
				if(hasChild) {
					m.put("icon", "fa fa-folder-o");
					m.remove("state");
				}else {
					m.put("icon", "fa fa-file-o");
				}
			}

		});
		Map<String, Object> m = new HashMap<String, Object>();
		m.put("id", "root");
		m.put("text", "系统菜单树");
		m.put("icon", " fa fa-home");
		Map<String, Object> state = new HashMap<String, Object>();
		state.put("disabled", true);
		m.put("state", state);
		m.put("children", ret);

		return m;
	}

	/**
	 * 给用户授权菜单
	 * @param userId
	 * @param menuIds
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveUserMenu(Integer userId, String menuIds) {
		//删除以前数据
		String delSql = "delete from user_menu_rela where user_id = " + userId ;
		daoHelper.execute(delSql);

		String[] ids = menuIds.split(",");
		String sql = "insert into user_menu_rela(user_id, menu_id) values(?,?)";
		for(final String tmp : ids){
			if(tmp.length() > 0){
				daoHelper.execute(sql, new PreparedStatementCallback<Object>(){
					public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
						ps.setInt(1, userId);
						ps.setInt(2, new Integer(tmp));
						ps.executeUpdate();
						return null;
					}
				});
			}
		}
		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		pms.put("oper", "给用户授权菜单，菜单ID:"+menuIds+", 用户ID: "+userId);
		pms.put("bsType", 7);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}
}
