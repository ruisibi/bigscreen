/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.bigscreen;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.bigscreen.BigScreen;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.entity.report.ShareUrl;
import com.ruisitech.bi.service.bigscreen.BigScreenService;
import com.ruisitech.bi.service.frame.LoggerService;
import com.ruisitech.bi.service.report.ShareUrlService;
import com.ruisitech.bi.util.BaseController;
import com.ruisitech.bi.util.IsModel;
import com.ruisitech.bi.util.RSBIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据大屏
 * @ClassName IndexController
 * @Description IndexController
 * @Author huangqin
 * @Date 2021/3/11 10:38 上午
 */
@Controller("bigscreenIndex")
@RequestMapping(value = "/bigscreen")
public class IndexController extends BaseController {

    @Autowired
    private BigScreenService service;

    @Autowired
    private ShareUrlService shareService;

    @Autowired
    private LoggerService loggerService;

    @RequestMapping(value="/getJson.action")
    public @ResponseBody
    Object get(Integer id){
        BigScreen bs = service.selectByPrimaryKey(id);
        if(bs == null){
            return super.buildError("大屏不存在.");
        }
        return super.buildSucces(bs.getPageInfo());
    }

    @RequestMapping(value="/list.action")
    public @ResponseBody
    Object list(@RequestBody PageParam page){
        if(page != null && page.getPage() != null && page.getRows() != null){
            PageHelper.startPage(page.getPage(), page.getRows());
        }
        List<BigScreen> ls = service.list(page.getSearch());
        PageInfo<BigScreen> pageInfo=new PageInfo<>(ls);
        return super.buildSucces(pageInfo);
    }

    @RequestMapping(value="/listAll.action")
    public @ResponseBody
    Object listAll(){
        List<BigScreen> ls = service.list(null);
        return super.buildSucces(ls);
    }

    @RequestMapping(value="/save.action")
    public @ResponseBody
    Object save(BigScreen bs){
        if(bs.getId() == null){
            service.insertSelective(bs);
        }else{
            bs.setUpdatedate(new Date());
            service.updateByPrimaryKeySelective(bs);
        }
        return super.buildSucces(bs.getId());
    }

    @RequestMapping(value="/delete.action")
    public @ResponseBody
    Object delete(Integer id){
        service.deleteByPrimaryKey(id);
        return super.buildSucces();
    }

    /**
    @RequestMapping(value="/Design.action")
    public @ResponseBody Object index(Integer id) {
        if(id != null) {
            BigScreen bs = service.selectByPrimaryKey(id);
            return super.buildSucces(bs.getPageInfo());
        }else{
            return super.buildError("文件不存在.");
        }
    }
     **/

    @RequestMapping(value="/View-{id}.action")
    public @ResponseBody Object view(@PathVariable("id") Integer id, HttpServletRequest req) {
        if(id == null){
            return super.buildNotFindError("文件不存在.");
        }
        BigScreen bs = service.selectByPrimaryKey(id);
        if(bs == null){
            return super.buildNotFindError("文件不存在.");
        }

        //记日志
        boolean is3g = IsModel.check(req);
        Map<String, Object> params = new HashMap<>();
        User u = RSBIUtils.getLoginUserInfo();
        params.put("userId", u.getUserId());
        params.put("userName", u.getLoginName());
        params.put("reportId", id);
        params.put("reportName", bs.getPageName());
        params.put("type", "bigscreen");
        params.put("income", is3g?"mobile":"pc");
        loggerService.insertReportLog(params);

        return super.buildSucces(bs.getPageInfo());
    }

    @RequestMapping(value="/copyUrl.action")
    public @ResponseBody
    Object copyUrl(ShareUrl dto){
        dto.setrType(3);
        shareService.saveShareUrl(dto);
        return super.buildSucces(dto.getToken());
    }

    @RequestMapping(value="/rename.action", method = RequestMethod.POST)
    public @ResponseBody
    Object rename(BigScreen bigScreen) {
        service.rename(bigScreen);
        return this.buildSucces();
    }

    @RequestMapping(value="/copy.action", method = RequestMethod.POST)
    public @ResponseBody
    Object copy(BigScreen bigScreen) {
        service.copy(bigScreen);
        return this.buildSucces();
    }
}
