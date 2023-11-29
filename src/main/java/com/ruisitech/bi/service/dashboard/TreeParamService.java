/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.dashboard;

import com.rsbi.ext.engine.tree.TreeService;
import com.rsbi.ext.engine.view.exception.ExtConfigException;
import com.ruisitech.bi.entity.bireport.TableInfoVO;
import com.ruisitech.bi.entity.dashboard.TreeQueryDto;
import com.ruisitech.bi.service.bireport.TableCacheService;
import com.ruisitech.bi.service.etl.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形框参数
 * @Description TreeParamService
 * @Author huangqin
 * @Date 2022/7/27 10:53 上午
 */
@Service
public class TreeParamService {

    @Autowired
    private TableCacheService cahceService;

    @Autowired
    private DataSourceService dataSourceService;

    public List<Map<String, Object>> queryTreeDatas(TreeQueryDto dto) throws ExtConfigException {
        TableInfoVO tvo = cahceService.getTableInfo(dto.getMatchTable());

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(dto.getTableIdCol());
        sql.append(",");
        sql.append(dto.getTablePidCol());
        sql.append(",");
        sql.append(dto.getTableNameCol());
        sql.append(" from ");
        sql.append(tvo.getTname());
        sql.append(" group by ");
        sql.append(dto.getTableIdCol());
        sql.append(",");
        sql.append(dto.getTablePidCol());
        sql.append(",");
        sql.append(dto.getTableNameCol());

        List<Map<String, Object>> ls = dataSourceService.querySqlUseDataSource(sql.toString(), null, tvo.getDsource());
        for(Map<String, Object> map : ls){
            map.put("id", map.get(dto.getTableIdCol()));
            map.put("text", map.get(dto.getTableNameCol()));
            map.put("icon", "fa fa-list-ul");
        }
        List<Map<String, Object>> roots = null;
        String tablePidValue = dto.getTablePidValue();
        if(StringUtils.isEmpty(tablePidValue)){
            roots = this.getChildren(ls, "0", dto);  //默认上级，0
        }else{
            roots = this.getChildren(ls, tablePidValue, dto);  //默认上级，仪表盘传过来的orgid值
        }
        this.loopChildren(roots, ls, dto);
        return roots;
    }

    private void loopChildren(List<Map<String, Object>> nodes, List<Map<String, Object>> datas, TreeQueryDto dto){
        for(int i=0; i<nodes.size(); i++){
            Map<String, Object> root = nodes.get(i);
            String id = null ;
            Object ret = root.get(dto.getTableIdCol());
            if(ret != null){
                id = ret.toString();
            }
            if(id !=  null){
                List<Map<String, Object>> child = this.getChildren(datas, id, dto);
                if(child.size() > 0){
                    this.loopChildren(child, datas, dto);
                }
                if(child.size() > 0){
                    Map<String, Object> m = new HashMap<>();
                    m.put("opened", true);
                    root.put("state", m);
                }
                root.put("children", child);
            }
        }
    }

    private List<Map<String, Object>> getChildren(List<Map<String, Object>> datas, String id, TreeQueryDto dto){
        List<Map<String, Object>> roots = new ArrayList<>();
        for(int i=0; i<datas.size(); i++){
            Map<String, Object> m = datas.get(i);
            String pid = null;
            Object pobj = m.get(dto.getTablePidCol());
            if(pobj != null){
                pid = pobj.toString();
            }
            if(pid != null && pid.equals(id)){
                roots.add(m);
            }
        }
        return roots;
    }
}
