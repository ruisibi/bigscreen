/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.bigscreen;

import com.alibaba.fastjson.JSONObject;
import com.ruisitech.bi.entity.bigscreen.BigScreen;
import com.ruisitech.bi.entity.frame.Menu;
import com.ruisitech.bi.mapper.bigscreen.BigScreenMapper;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.service.frame.MenuService;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大屏Service
 * @ClassName BigScreenService
 * @Description BigScreenService
 * @Author huangqin
 * @Date 2021/3/11 3:10 下午
 */
@Service
public class BigScreenService {

    @Resource
    private BigScreenMapper mapper;

    @Autowired
    private LoggerService loggerService;

    @Autowired
    private MenuService menuService;

    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Integer id){
        BigScreen record = mapper.selectByPrimaryKey(id);
        //删除菜单
        List<Menu> menu = menuService.getMenuByUrl("/bigscreen/PushView?bsid=" + id);
        menuService.deleteMenus(menu);

        //记录日志
        Map<String, Object> pms = new HashMap<>();
        pms.put("id", record.getId());
        pms.put("name", record.getPageName());
        pms.put("operType", 3);
        pms.put("oper", "删除大屏" + record.getPageName());
        pms.put("bsType", 12);
        loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());

        return mapper.deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public int insertSelective(BigScreen record){
        //记录日志
        Map<String, Object> pms = new HashMap<>();
        pms.put("id", record.getId());
        pms.put("name", record.getPageName());
        pms.put("operType", 1);
        pms.put("oper", "新建大屏" + record.getPageName());
        pms.put("bsType", 12);
        loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());

        record.setId(mapper.maxBigScreenId());
        record.setCreatedate(new Date());
        record.setUpdatedate(new Date());
        record.setUserId(RSBIUtils.getLoginUserInfo().getUserId());
        //更新ID
        JSONObject obj = JSONObject.parseObject(record.getPageInfo());
        obj.put("id", record.getId());
        record.setPageInfo(obj.toJSONString());
        return mapper.insertSelective(record);
    }

    public BigScreen selectByPrimaryKey(Integer id){
        return mapper.selectByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(BigScreen record){
        //记录日志
        Map<String, Object> pms = new HashMap<>();
        pms.put("id", record.getId());
        pms.put("name", record.getPageName());
        pms.put("operType", 2);
        pms.put("oper", "修改大屏" + record.getPageName());
        pms.put("bsType", 12);
        loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());

        return mapper.updateByPrimaryKeySelective(record);
    }

    public List<BigScreen> list(String search){
        Integer userId = null;
        return mapper.list(userId, search);
    }

    public int rename(BigScreen record){
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Transactional(rollbackFor = Exception.class)
    public int copy(BigScreen record){
        BigScreen old = mapper.selectByPrimaryKey(record.getId());
        record.setId(mapper.maxBigScreenId());
        record.setCreatedate(new Date());
        record.setUpdatedate(new Date());
        record.setUserId(RSBIUtils.getLoginUserInfo().getUserId());

        //记录日志
        Map<String, Object> pms = new HashMap<>();
        pms.put("id", record.getId());
        pms.put("name", record.getPageName());
        pms.put("operType", 1);
        pms.put("oper", "复制大屏" + record.getPageName());
        pms.put("bsType", 12);
        loggerService.insertBussLog(pms, RSBIUtils.getLoginUserInfo());

        //更新ID
        JSONObject obj = JSONObject.parseObject(old.getPageInfo());
        obj.put("id", record.getId());
        record.setPageInfo(obj.toJSONString());
        return mapper.insertSelective(record);
    }
}
