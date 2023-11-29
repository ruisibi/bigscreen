/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.model;

import com.ruisitech.bi.entity.model.TableMeta;
import com.ruisitech.bi.mapper.model.TableMeasureMapper;
import com.ruisitech.bi.mapper.model.TableMetaMapper;
import com.ruisitech.bi.util.RSBIUtils;
import com.ruisitech.bi.util.TreeInterface;
import com.ruisitech.bi.util.TreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对 olap_table_meta 表操作的 service
 * 实际上，一个表对应一个立方体(cube),也对应一个分析主题(subject)
 * @author hq
 *
 */
@Service
public class TableMetaServcice {

	@Autowired
	private TableMetaMapper tableMapper;

	@Autowired
	private TableMeasureMapper kpiMapper;




	/**
	 * 获取立方体的 tree 结构
	 * @param selectDsIds
	 * @return
	 */
	public Object getCubeTree(String selectDsIds){
		//默认所有节点都打开
		Map<String, Object> state = new HashMap<String, Object>();
		state.put("opened", true);

		Map<String, Object> curGroup = null; //当前分组对象.
		Map<String, Object> curKpigroup = null; //当前指标分类
		if(selectDsIds == null || selectDsIds.length() == 0){
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("id", "root");
			root.put("text", "您还未选择数据模型！");
			root.put("state", state);
			root.put("icon", "fa fa-warning");
			root.put("children", new ArrayList<Object>());
			return root;

		}else{
			List<Map<String, Object>> ls = tableMapper.listDs(selectDsIds);
			for(int i=0; i<ls.size(); i++){
				Map<String, Object> m = ls.get(i);
				m.put("state", state);
				String tid = m.get("id").toString();
				//给数据集节点添加维度、指标节点
				List<Object> nodeChildren = new ArrayList<Object>();
				m.put("children", nodeChildren);
				Map<String, Object> wdnode = new HashMap<String, Object>();
				wdnode.put("id", "wd");
				wdnode.put("text", "维度");
				wdnode.put("state", state);
				wdnode.put("icon", "fa fa-gears");
				List<Object> wdChindren = new ArrayList<Object>();
				wdnode.put("children", wdChindren);
				nodeChildren.add(wdnode);
				Map<String, Object> zbnode = new HashMap<String, Object>();
				zbnode.put("id", "zb");
				zbnode.put("text", "度量");
				zbnode.put("state", state);
				zbnode.put("icon", "glyphicon glyphicon-signal");
				List<Object> zbChindren = new ArrayList<Object>();
				zbnode.put("children", zbChindren);
				nodeChildren.add(zbnode);



				List<Map<String, Object>> children = tableMapper.listDsMeta(new Integer(tid));

				//设置attributes;
				for(int j=0; j<children.size(); j++){
					Map<String, Object> child = children.get(j);
					int colType = new Integer(child.get("col_type").toString());
					String grouptype = (String)child.get("grouptype");
					if(grouptype == null || grouptype.length() == 0){
						grouptype = null;
					}
					String groupname = (String)child.get("groupname");
					if(grouptype != null && grouptype.length() > 0 && colType == 1){  //维度
						if(curGroup == null || !curGroup.get("id").equals(grouptype)){
							//添加分组节点
							Map<String, Object> fz = new HashMap<String, Object>();
							fz.put("id", grouptype);
							fz.put("text", groupname);
							fz.put("state", state);
							fz.put("icon", " glyphicon glyphicon-stop icon_dim");
							fz.put("children", new ArrayList<Map<String, Object>>());
							wdChindren.add(fz);
							curGroup = fz;
						}
					}else{
						curGroup = null;
					}

					if(grouptype != null && grouptype.length() > 0 && colType == 2){  //度量
						if(curKpigroup == null || !curKpigroup.get("id").equals(grouptype)){
							//添加分组节点
							Map<String, Object> fz = new HashMap<String, Object>();
							fz.put("id", grouptype);
							fz.put("text", groupname);
							fz.put("state", state);
							fz.put("icon", "glyphicon glyphicon-folder-open icon_kpi");
							fz.put("children", new ArrayList<Map<String, Object>>());
							zbChindren.add(fz);
							curKpigroup = fz;
						}
					}else{
						curKpigroup = null;
					}

					Map<String, Object> attr = new HashMap<String, Object>();
					child.put("li_attr", attr);
					attr.put("col_type", colType);
					attr.put("col_id", child.get("col_id"));
					attr.put("col_name", child.get("col_name"));
					attr.put("tid", child.get("tid"));
					attr.put("tname", m.get("tname"));
					attr.put("alias", child.get("alias"));
					attr.put("fmt", child.get("fmt") == null ? "" : child.get("fmt"));
					attr.put("aggre", child.get("aggre"));
					attr.put("dim_type", child.get("dim_type"));
					attr.put("tableName", child.get("tableName") == null ? "" : child.get("tableName"));
					attr.put("tableColKey", child.get("tableColKey") == null ? "" : child.get("tableColKey"));
					attr.put("tableColName", child.get("tableColName") == null ? "" : child.get("tableColName"));
					attr.put("dateformat", child.get("dateformat") == null ? "" : child.get("dateformat"));
					attr.put("distinctCol", child.get("distinctCol") == null ? "" : child.get("distinctCol"));
					attr.put("kpiDesc", child.get("kpiDesc") == null ? "" : child.get("kpiDesc"));
					attr.put("income", child.get("income"));
					if(curGroup == null){
						attr.put("iscas", "");
					}else{
						attr.put("iscas", "y");
					}
					attr.put("fromCol", child.get("fromCol") == null ? "" : child.get("fromCol"));
					attr.put("dimord", child.get("dimord") == null ? "" : child.get("dimord"));
					attr.put("rate", child.get("rate"));
					attr.put("unit", child.get("unit") == null ? "" : child.get("unit"));
					attr.put("grouptype", grouptype);
					attr.put("calc_kpi", child.get("calc_kpi"));
					attr.put("valType", child.get("valType"));
					attr.put("ordcol", child.get("ordcol") == null ? "" : child.get("ordcol"));
					attr.put("name", child.get("text"));
					//设置节点图标
					if(colType == 1){
						if(grouptype == null || grouptype.length() == 0){
							child.put("icon", "glyphicon glyphicon-stop icon_dim");
						}else{
							child.put("icon", "fa fa-th-large icon_dim");
						}
					}else{
						child.put("icon", "glyphicon glyphicon-stop icon_kpi");
					}
					if(colType == 1){  //维度
						if(curGroup == null){
							//如果是父子维度，需要对维度进行自动扩展
							String ispcdim = (String)child.get("ispcdim");
							if("y".equals(ispcdim)) {
								Integer pcdimlevel = new Integer(child.get("pcdimlevel").toString());
								List<Map<String, Object>> pcList = new ArrayList<Map<String, Object>>();
								for(int k=0; k<pcdimlevel; k++) {
									HashMap<String, Object> c = (HashMap<String, Object>)child;
									HashMap<String, Object> c2 = (HashMap<String, Object>)c.clone();
									c2.put("icon", "fa fa-th-large icon_dim");
									c2.put("text", (k + 1)+"级");
									String colId = c2.get("col_id").toString();
									int newColId = Integer.parseInt(colId+(k+1));
									c2.put("col_id", newColId);
									c2.put("id", newColId);
									//设置 level
									HashMap<String, Object> pcAttr = (HashMap<String, Object>)c2.get("li_attr");
									pcAttr = (HashMap<String, Object>)pcAttr.clone();
									pcAttr.put("pclevel", (k + 1));
									pcAttr.put("col_id", newColId);
									pcAttr.put("levelCol", c2.get("levelCol"));
									pcAttr.put("ispcdim", c2.get("ispcdim"));
									pcAttr.put("pcId", c2.get("pcId"));
									pcAttr.put("pcPid", c2.get("pcPid"));
									pcAttr.put("alias", c2.get("alias").toString() + "0"+ (k + 1));
									pcAttr.put("ordcol", c2.get("ordcol").toString() + "0" + (k + 1));
									pcAttr.put("grouptype", "pcdim"); //父子维度
									c2.put("li_attr", pcAttr);
									pcList.add(c2);
								}
								Map<String, Object> nd = new HashMap<String, Object>();
								nd.put("id", child.get("col_id"));
								nd.put("text", child.get("text"));
								nd.put("icon", "glyphicon glyphicon-stop icon_dim");
								nd.put("state", state);
								nd.put("children", pcList);
								wdChindren.add(nd);
							}else {
								wdChindren.add(child);
							}
						}else{
							((List)curGroup.get("children")).add(child);
						}
					}else if(colType == 2){  //度量
						String kpiDesc = (String) child.get("kpiDesc");  //指标解释
						if(kpiDesc != null && kpiDesc.length() > 0){
							String text = (String)child.get("text");
							child.put("text",text + " <i class='fa fa-question'></i>");
						}
						if(curKpigroup == null){
							zbChindren.add(child);
						}else{
							((List)curKpigroup.get("children")).add(child);
						}

					}
					if(colType == 1){ //维度，给ID 加d
						 child.put("id", "d" + child.get("id"));
					}else if(colType == 2){ //度量，给ID 加 k
						child.put("id", "k" + child.get("id"));
					}
				}
			}
			return ls;
		}
	}

	/**
	 * 查询指标的指标解释
	 * @param selectDsIds
	 * @return
	 */
	public List<Map<String, Object>> listKpiDesc(String selectDsIds){
		if(selectDsIds == null || selectDsIds.trim().length() == 0){
			return new ArrayList<Map<String, Object>>();
		}
		return kpiMapper.listKpiDesc(selectDsIds);
	}

	public TableMeta getTable(Integer tid) {
		return tableMapper.getTable(tid);
	}

	public List<Map<String, Object>> applistSubject(){
		List<Map<String, Object>> ls = tableMapper.applistSubject();
		TreeService ser = new TreeService();
		List<Map<String, Object>> ret = ser.createTreeData(ls, new TreeInterface(){

			@Override
			public void processMap(Map<String, Object> m) {

			}

			@Override
			public void processEnd(Map<String, Object> m, boolean hasChild) {
				String tp = (String)m.get("tp");
				if("ds".equals(tp)){
					int id = Integer.parseInt(m.get("id").toString());
					m.put("id", id - 1000);
				}
			}

		});
		return ret;
	}
}
