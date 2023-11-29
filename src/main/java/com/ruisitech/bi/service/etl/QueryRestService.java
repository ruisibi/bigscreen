/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.grid.PageInfo;
import com.rsbi.ext.engine.wrapper.ExtRequest;
import com.rsbi.ispire.dc.grid.DatasetProvider;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.etl.EtlRestTableMetaParam;
import com.ruisitech.bi.entity.etl.EtlTableMeta;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.util.OgnlWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 查询Rest的服务类
 * @author zxd
 * @Date 2020年3月24日
 */
@Service
public class QueryRestService implements DatasetProvider {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private TableCacheService cahceService;

	private static Logger log = LogManager.getLogger(QueryRestService.class);

	public List<Object> queryRestColsAndDatas(TableInfoVO tinfo, int top){
		List<Object> ret = new ArrayList<Object>();
		//查询列
		List<String> cols = new ArrayList<String>();
		tinfo.getCols().forEach((o) -> cols.add(o.getColName()));
		//查询数据
		List<Map<String, Object>> ls = queryRestApi(tinfo, null, null);
		ret.addAll(ls);
		ret.add(0, cols);
		return ret;
	}

	public List<Map<String, Object>> queryRestApi(EtlTableMeta table, Map<String, Object> input, PageInfo page) {
		TableInfoVO tvo = new TableInfoVO();
		tvo.setRestPostType(table.getRestPostType());
		tvo.setRestParam(table.getRestParam());
		tvo.setRestDataKey(table.getRestDataKey());
		tvo.setRestUrl(table.getRestUrl());
		tvo.setRestTotalKey(table.getRestTotalKey());
		return this.queryRestApi(tvo, input, page);
	}


	public List<Map<String, Object>> queryRestApi(TableInfoVO tinfo, Map<String, Object> input, PageInfo page) {
		List<Map<String, Object>> result = null;
		try {
			String postType = tinfo.getRestPostType();
			List<EtlRestTableMetaParam> params = tinfo.getRestParam();
			String dataKey = tinfo.getRestDataKey();
			if("GET".equalsIgnoreCase(postType)) {
				StringBuffer url = new StringBuffer(tinfo.getRestUrl());
				url.append("?");
				//拼接参数
				params.forEach(p -> {
					//处理输入参数
					Object pval = null;
					if(input != null) {
						pval = input.get(p.getParamName());
					}
					if(pval == null && p.getParamDefvalue() != null && p.getParamDefvalue().length() > 0) {  //采用默认值
						pval = p.getParamDefvalue();
					}
					url.append(p.getParamName());
					url.append("=");
					url.append(pval == null ?"" : pval);
					url.append("&");
				});
				String u = url.substring(0, url.length() - 1);
				log.info(u);
				String response = restTemplate.getForObject(u, String.class);
				JSONObject datas = JSON.parseObject(response);
				OgnlWrapper wrapper = new OgnlWrapper(datas);
				if(page != null){
					Integer total = wrapper.getInt(tinfo.getRestTotalKey());
					if(total ==  null){
						throw new RuntimeException("Rest接口未返回 总记录数 字段名： "+tinfo.getRestTotalKey()+"。");
					}
					page.setAllsize(total);
				}
				/**
				JSONArray json =  (JSONArray)datas.get(dataKey);
				if(json == null) {
	    			return new ArrayList<Map<String, Object>>();
	    		}
	    		**/
				//return (List)json;

				Object obj = wrapper.get(dataKey);
				if(obj == null) {
					result = new ArrayList<Map<String, Object>>();
				}
				if(obj instanceof JSONArray) {
					JSONArray dts = (JSONArray)obj;
					result = (List)dts;
				}else if(obj instanceof JSONObject){
					List<Map<String, Object>> ret = new ArrayList<>();
					ret.add((Map)obj);
					result = ret;
				}else {
					result =  new ArrayList<Map<String, Object>>();
				}

			}else if("POST".equalsIgnoreCase(postType)){
				Map<String, Object> pms = new HashMap<>();
				//拼接参数
				params.forEach(p -> {
					String pval = null;
					if(input != null) {
						Object v = input.get(p.getParamName());
						pval = v == null ? "": v.toString();
					}
					if((pval == null || pval.length() == 0) && p.getParamDefvalue() != null && p.getParamDefvalue().length() > 0) {  //采用默认值
						pval = p.getParamDefvalue();
					}
					if(pval != null && pval.length() > 0) {
						if("Number".equals(p.getParamType())) {
							pms.put(p.getParamName(), new Integer(pval));
						}else {
							pms.put(p.getParamName(), pval);
						}
					}else {
						pms.put(p.getParamName(), "");
					}
				});
				String u = tinfo.getRestUrl();
				//打印sql
				if(log.isInfoEnabled()) {
					StringBuffer sb = new StringBuffer();
					pms.keySet().forEach(o->{
						sb.append("&");
						sb.append(o);
						sb.append("=");
						sb.append(pms.get(o));
					});
					log.info(u + sb);
				}
				HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(pms), headers);
				String s= restTemplate.postForEntity(u,formEntity,String.class).getBody();
	    		JSONObject datas = JSONObject.parseObject(s);
				OgnlWrapper wrapper = new OgnlWrapper(datas);
				if(page != null){
					Integer total = wrapper.getInt(tinfo.getRestTotalKey());
					if(total == null){
						throw new RuntimeException("Rest接口未返回数据量字段，字段名是: "+tinfo.getRestTotalKey()+" 。");
					}
					page.setAllsize(total);
				}
	    		Object obj = wrapper.get(dataKey);
				if(obj == null) {
					result = new ArrayList<>();
				}
				if(obj instanceof JSONArray) {
					JSONArray dts = (JSONArray)obj;
					result = (List)dts;
				}else if(obj instanceof JSONObject){
					List<Map<String, Object>> ret = new ArrayList<>();
					ret.add((Map)obj);
					result = ret;
				}else {
					result = new ArrayList<>();
				}
			}else {
				throw new RuntimeException(postType+"不支持。");
			}

		}catch(HttpServerErrorException ex) {
			log.error("Rest查询出错：" + ex.getResponseBodyAsString(), ex);
			throw ex;
		}
		return result;
	}

	/**
	 * 用来查询rest接口，其中sql封装成 tid?xx=xx&cc=cc形式
	 */
	@Override
	public List<Map<String, Object>> queryData(String sql, boolean isAgg, String master, Integer masterId,
			ExtRequest request, PageInfo page) throws Exception {
		Integer tid = new Integer(master);
		TableInfoVO tinfo = cahceService.getTableInfo(tid);
		//处理参数
		Map<String, Object> p = new HashMap<>();
		int pos = sql.indexOf("?");
		if(pos >= 0) {
			String pms = sql.substring(pos + 1, sql.length());
			String[] pstr = pms.split("&");
			Arrays.stream(pstr).forEach(s->{
				if(s.trim().length() > 0) {
					String[] pp = s.split("=");
					p.put(pp[0], pp.length > 1 ? pp[1] : "");
				}
			});
		}
		//处理分页
		if(page != null) {
			Integer curPage = page.getCurtpage();
			Integer pageSize = page.getPagesize();
			//ext 框架的分页默认curPage 为 0， 如果是0需要替换成 null
			if(curPage == 0){
				curPage = null;
			}else{
				curPage++;  //为兼容第一个页从1开始的分页，需要给页码数加1
			}
			//查询 curPage 参数
			List<EtlRestTableMetaParam> params = tinfo.getRestParam();
			if(params == null || params.size() == 0){
				throw new RuntimeException("接口未定义分页参数(curPage/pageSize)");
			}
			EtlRestTableMetaParam curPageParam = params.stream().filter(m->m.getParamisCurPage() != null && m.getParamisCurPage()).findFirst().orElseThrow(()->new RuntimeException("未定义当前页参数"));
			p.put(curPageParam.getParamName(), curPage);
			EtlRestTableMetaParam pageSizeParam = params.stream().filter(m->m.getParamisPageSize() != null && m.getParamisPageSize()).findFirst().orElseThrow(()->new RuntimeException("未定义pageSize参数"));
			p.put(pageSizeParam.getParamName(), pageSize);
		}
		List<Map<String, Object>> ret = queryRestApi(tinfo, p, page);

		return ret;
	}
}
