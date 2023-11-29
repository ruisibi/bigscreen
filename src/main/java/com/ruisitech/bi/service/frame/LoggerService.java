/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.frame;

import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 系统日记记录
 * @Author huangqin
 * @Date 2020/10/20 9:11 下午
 */
@Service
public class LoggerService {

    @Autowired
    private DaoHelper sysDaoHelper;




    @Value("${spring.datasource.dbType}")
    private String dbType;


    /**
     * 报表访问日志
     * @param params
     */
    public void insertReportLog(Map<String, Object> params){
        String sql = "insert into sc_report_logs(user_id, user_name, report_id, report_name," +
                "report_type, visit_date, income) values(?,?,?,?,?,"+RSBIUtils.getDbDateFunction(dbType)+", ?)";
        sysDaoHelper.execute(sql, ps -> {
            ps.setInt(1, (Integer) params.get("userId"));
            ps.setString(2, (String) params.get("userName"));
            ps.setString(3, params.get("reportId").toString());
            ps.setString(4, (String) params.get("reportName"));
            ps.setString(5, (String) params.get("type"));
            ps.setString(6, (String) params.get("income"));
            ps.executeUpdate();
            return null;
        });
    }


    /**
     * 用户登录日志
     * @param params
     */
    public void insertLoginLog(Map<String, Object> params){
        String sql = "insert into sc_login_logs(user_id, user_name, log_date, log_state, income) values(?,?,"+RSBIUtils.getDbDateFunction(dbType)+", ?, ?)";
        sysDaoHelper.execute(sql, ps -> {
            ps.setInt(1, (Integer) params.get("userId"));
            ps.setString(2, (String) params.get("userName"));
            ps.setInt(3, (Integer) params.get("state"));
            ps.setString(4, (String) params.get("income"));
            ps.executeUpdate();
            return null;
        });
    }

    /**
     * 表日志
     * @param params
     */
    public void insertTableLog(Map<String, Object> params, User u){
        if(u == null){
            return;
        }
        String sql = "insert into sc_table_logs(table_id, table_name, table_oper_type, table_oper, oper_date,user_id, user_name) " +
                "values(?,?,?,?,"+RSBIUtils.getDbDateFunction(dbType)+", ?, ?)";
        sysDaoHelper.execute(sql, ps -> {
            ps.setInt(1,  params.get("tid") == null ?  null : (Integer) params.get("tid"));
            ps.setString(2, (String) params.get("tname"));
            ps.setInt(3, (Integer) params.get("operType"));
            ps.setString(4, (String) params.get("oper"));
            ps.setInt(5, u.getUserId());
            ps.setString(6, u.getLoginName());
            ps.executeUpdate();
            return null;
        });
    }

    /**
     * 业务对象日志
     * @param params
     * @param u
     */
    public void insertBussLog(Map<String, Object> params, User u){
        if(u == null){
            return;
        }
        String sql = "insert into sc_buss_log(model_id, model_name, model_oper_type, model_oper, oper_date,user_id, user_name, buss_type) " +
                "values(?,?,?,?,"+RSBIUtils.getDbDateFunction(dbType)+", ?, ?,?)";
        sysDaoHelper.execute(sql, ps -> {
            String id = params.get("id") == null ? null : params.get("id").toString();
            ps.setString(1, id);
            String name = params.get("name") == null ? null : params.get("name").toString();
            ps.setString(2, name);
            ps.setInt(3, (Integer) params.get("operType"));
            ps.setString(4, (String) params.get("oper"));
            ps.setInt(5, u.getUserId());
            ps.setString(6, u.getLoginName());
            ps.setInt(7, (Integer) params.get("bsType"));
            ps.executeUpdate();
            return null;
        });
    }
}
