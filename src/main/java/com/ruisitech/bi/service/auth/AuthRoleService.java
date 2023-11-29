/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.auth;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.frame.Role;
import com.ruisitech.bi.mapper.frame.RoleMapper;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.bi.util.TreeInterface;
import com.ruisitech.bi.util.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限管理 - 角色管理模块
 * @author gdp
 *
 */
@Service
public class AuthRoleService {

	@Resource
	private RoleMapper mapper;

	@Resource(name = "sysDaoHelper")
	private DaoHelper daoHelper;

	@Autowired
	private LoggerService loggerService;


	public Map<String, Object> listRoleMenus(Integer roleId){
		List<Map<String, Object>> menus = mapper.listRoleMenus(roleId);
		TreeService tree = new TreeService();
		List<Map<String, Object>> ret = tree.createTreeData(menus, new TreeInterface(){
			@Override
			public void processMap(Map<String, Object> m) {
				Object id2 = m.get("id2");
				if(id2 != null){
					Map<String, Object> state = new HashMap<String, Object>();
					state.put("selected", true);
					m.put("state", state);
				}

			}

			@Override
			public void processEnd(Map<String, Object> m, boolean hasChild) {
				if(hasChild){
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
		m.put("icon", "fa fa-home");
		m.put("children", ret);
		return m;
	}

	public List<Map<String, Object>> roledata(Integer roleId){
		List<Map<String, Object>> datas = mapper.roledata(roleId);

		Function<Map<String, Object>, Map<String, Object>> exec = (m)->{
			Integer dataId = (Integer)m.get("dataId");
			if(dataId != null){
				Map<String, Object> state = new HashMap<String, Object>();
				state.put("selected", true);
				m.put("state", state);
			}
			return m;
		};

		List<Map<String, Object>> ret = new ArrayList<>();
		Map<String, List<Map<String, Object>>> complexMap = datas.stream().collect(Collectors.groupingBy(m->(String)m.get("income")));
		for(Map.Entry<String, List<Map<String, Object>>> ent : complexMap.entrySet()){
			Map<String, Object> imp = new HashMap<>();
			imp.put("id", ent.getKey());
			imp.put("text", ent.getKey());
			imp.put("icon", "fa fa-navicon");
			imp.put("children", ent.getValue());
			for(Map<String, Object> m : ent.getValue()){
				exec.apply(m);
			}
			ret.add(imp);
		}

		return ret;
	}

	public 	List<Role> listUserHasRole(Integer userId){
		return mapper.listUserHasRole(userId);
	}

	public List<Role> list(String keyword){
		return mapper.list(keyword);
	}

	/**
	 * 查询所有角色及用户所有的角色
	 * @param userId
	 * @return
	 */
	public List<Role> listUserRole(Integer userId){
		return mapper.listUserRole(userId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void addUserRole(Integer[] roleIds, Integer userId) {
		//删除角色
		daoHelper.execute("delete from role_user_rela where user_id = " + userId);
		for(int i=0; roleIds != null && i<roleIds.length; i++)
		{
			Integer roleId = roleIds[i];
			daoHelper.execute("insert into role_user_rela(user_id,role_id) values("+userId+","+roleId+")");
		}

		//写日志
		Map<String, Object> pms = new HashMap<>();
		StringBuffer rs = new StringBuffer();
		if(roleIds != null) {
			for (Integer r : roleIds) {
				rs.append(r);
				rs.append(",");
			}
		}
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		pms.put("oper", "给用户授权角色，角色ID:"+rs+" 用户ID: "+userId);
		pms.put("bsType", 7);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void saveRole(Role role) {
		int idType = role.getIdType();
		String dateKey = role.getDateString();
		String sql = "insert into sc_role(role_name,role_desc,create_date,create_user, ord"+(idType==2?",role_id":"")+") values(?,?,"+dateKey+",?,?"+(idType==2?",?":"")+")";
		daoHelper.execute(sql, new PreparedStatementCallback<Object>(){
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setString(1, role.getRoleName());
				ps.setString(2, role.getRoleDesc());
				ps.setString(3, RSBIUtils.getLoginUserInfo().getLoginName());
				ps.setInt(4, role.getOrd());
				if(idType == 2){
					int maxid = daoHelper.queryForInt("select case WHEN max(role_id) is null then 1 else  max(role_id) + 1 end id from sc_role");
					ps.setInt(5, maxid);
				}
				ps.executeUpdate();
				return null;
			}
		});
		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", role.getRoleId());
		pms.put("name", role.getRoleName());
		pms.put("operType", 1);
		pms.put("oper", "创建角色" + role.getRoleName());
		pms.put("bsType", 11);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateRole(Role role) {
		String sql = "update sc_role set role_name = ?,role_desc = ?, ord=? where role_id = ?";
		daoHelper.execute(sql, new PreparedStatementCallback<Object>(){
			public Object doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
				ps.setInt(4, role.getRoleId());
				ps.setString(1, role.getRoleName());
				ps.setString(2, role.getRoleDesc());
				ps.setInt(3, role.getOrd());
				ps.executeUpdate();
				return null;
			}
		});
		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", role.getRoleId());
		pms.put("name", role.getRoleName());
		pms.put("operType", 2);
		pms.put("oper", "修改角色" + role.getRoleName());
		pms.put("bsType", 11);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteRole(Integer id) {
		Role role = this.getRole(id);
		String sql = "delete from sc_role where role_id = " + id ;
		daoHelper.execute(sql);
		//删除角色菜单关系
		daoHelper.execute("delete from role_menu_rela where role_id = " + id);
		//删除角色用户关系
		daoHelper.execute("delete from role_user_rela where role_id = " + id);

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", role.getRoleId());
		pms.put("name", role.getRoleName());
		pms.put("operType", 3);
		pms.put("oper", "删除角色" + role.getRoleName());
		pms.put("bsType", 11);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	public Role getRole(Integer id) {
		return mapper.getById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public void roleMenu(String menuIds, Integer roleId) {
		//已有菜单
		List<Map<String, Object>> hasMenus = daoHelper.queryForList("select menu_id from role_menu_rela where role_id = " + roleId);

		//删除以前数据
		String delSql = "delete from role_menu_rela where role_id = " + roleId;
		daoHelper.execute(delSql);
		StringBuffer sb = new StringBuffer("");
		if(menuIds.length() > 0) {
			String[] ids = menuIds.split(",");//处理获取的菜单ID格式
			String sql = "insert into role_menu_rela(role_id, menu_id) values(?,?)";
			daoHelper.execute(sql, ps -> {
				for (String tmp : ids) {
					ps.setInt(1, roleId);
					ps.setInt(2, new Integer(tmp));
					ps.addBatch();
				}
				ps.executeBatch();
				return null;
			});

			sb.append("授权菜单：");
			for(String id : ids){
				if(hasMenus.stream().noneMatch(m->m.get("menu_id").toString().equals(id))){
					sb.append(id);
					sb.append(",");
				}
			}
			sb.append("移除授权：");
			for(Map<String, Object> m : hasMenus){
				if(Arrays.stream(ids).noneMatch(id->id.equals(m.get("menu_id").toString()))){
					sb.append(m.get("menu_id"));
					sb.append(",");
				}
			}
		}else{
			sb.append("移除授权：");
			for(Map<String, Object> m : hasMenus){
				sb.append(m.get("menu_id"));
				sb.append(",");
			}
		}

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		pms.put("oper", "给角色授权菜单，"+sb.toString()+"角色ID: "+roleId);
		pms.put("bsType", 6);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	public void roleOneMenu(Integer menuId, Integer roleId){
		String sql = "insert into role_menu_rela(role_id, menu_id) values(?,?)";
		daoHelper.execute(sql, (ps)->{
			ps.setInt(1, roleId);
			ps.setInt(2, menuId);
			ps.executeUpdate();
			return null;
		});

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		pms.put("oper", "给角色授权菜单，菜单ID:"+menuId+", 角色ID: "+roleId);
		pms.put("bsType", 6);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void roleUserSave(Map<String, Object> opms){
		Integer roleId = (Integer) opms.get("roleId");
		List<Integer> users = (List<Integer>)opms.get("users");
		String sql = "insert into role_user_rela(user_id,role_id) values(?,?)";
		this.daoHelper.execute(sql, ps -> {
			for(Integer u : users){
				ps.setInt(1, u);
				ps.setInt(2, roleId);
				ps.addBatch();
			}
			ps.executeBatch();
			return null;
		});

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		pms.put("oper", "给角色添加用户，角色ID:"+roleId+"，用户ID: "+users);
		pms.put("bsType", 7);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void roleReportSave(Map<String, Object> opms){
		Integer roleId = (Integer) opms.get("roleId");
		//先删除
		String del = "delete from role_report_rela where role_id = " + roleId;
		daoHelper.execute(del);

		List<String> reports = (List<String>)opms.get("reports");
		String sql = "insert into role_report_rela(report_id,role_id) values(?,?)";
		this.daoHelper.execute(sql, ps -> {
			for(String r : reports){
				ps.setString(1, r);
				ps.setInt(2, roleId);
				ps.addBatch();
			}
			ps.executeBatch();
			return null;
		});

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		String s = reports.toString();
		if(s.length() >= 400){
			s = s.substring(0, 400);
		}
		pms.put("oper", "给角色授权报表，角色ID:"+roleId+"，报表ID: "+s);
		pms.put("bsType", 8);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void roleDashboardSave(Map<String, Object> opms){
		Integer roleId = (Integer) opms.get("roleId");
		//先删除
		String del = "delete from role_dashboard_rela where role_id = " + roleId;
		daoHelper.execute(del);

		List<Integer> reports = (List<Integer>)opms.get("dashboard");
		String sql = "insert into role_dashboard_rela(dashboard_id, role_id) values(?,?)";
		this.daoHelper.execute(sql, ps -> {
			for(Integer r : reports){
				ps.setInt(1, r);
				ps.setInt(2, roleId);
				ps.addBatch();
			}
			ps.executeBatch();
			return null;
		});

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		String s = reports.toString();
		if(s.length() >= 400){
			s = s.substring(0, 400);
		}
		pms.put("oper", "给角色授权仪表盘，角色ID:"+roleId+"，仪表盘ID: "+s);
		pms.put("bsType", 9);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	@Transactional(rollbackFor = Exception.class)
	public void roleDataSave(Integer roleId, String dataIds) {
		//删除以前数据
		String delsql = "delete from role_data_rela where role_id = " + roleId;
		this.daoHelper.execute(delsql);
		if(dataIds == null || dataIds.length() == 0) {
			return;
		}
		//批量插入
		String[] datas = dataIds.split(",");
		String sql = "insert into role_data_rela(role_id, data_id) values(?,?)";
		this.daoHelper.execute(sql, ps -> {
			for(String data : datas) {
				ps.setInt(1, roleId);
				ps.setInt(2, Integer.parseInt(data));
				ps.addBatch();
			}
			ps.executeBatch();
			return null;
		});

		//写日志
		Map<String, Object> pms = new HashMap<>();
		pms.put("id", null);
		pms.put("name", null);
		pms.put("operType", 2);
		pms.put("oper", "给角色授权表权限，表ID:"+dataIds+", 角色ID: "+roleId);
		pms.put("bsType", 6);
		loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());
	}

	/**
	 * 查询角色下的用户列表
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> listRoleUsers(Integer roleId){
		return mapper.listRoleUser(roleId);
	}
}
