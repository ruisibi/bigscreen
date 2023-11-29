/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.web.bigscreen;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruisitech.bi.entity.bigscreen.Resource;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.service.bigscreen.ResourceService;
import com.ruisitech.bi.util.BaseController;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 数据大屏资源管理类
 * 用来管理大屏使用的一些图片等资源文件
 */
@Controller
@RequestMapping(value = "/bigscreen")
public class ResourceController extends BaseController {

    private Logger logger = LogManager.getLogger(ResourceController.class);

    @Autowired
    private ResourceService service;

    @RequestMapping(value="/resource/list.action")
    public @ResponseBody
    Object list(PageParam page){
        PageHelper.startPage(page.getPage(), page.getRows());
        List<Resource> ls = service.list();
        PageInfo<Resource> pageInfo=new PageInfo<>(ls);
        return super.buildSucces(pageInfo);
    }

    @RequestMapping(value="/resource/ImgUpload.action", method = RequestMethod.POST)
    public @ResponseBody
    Object upLoad(String filename, HttpServletRequest request){
        try{
            //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
            CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(request.getSession().getServletContext());
            //检查form中是否有enctype="multipart/form-data"
            if(multipartResolver.isMultipart(request)) {
                //将request变成多部分request
                MultipartHttpServletRequest multiRequest= (MultipartHttpServletRequest)request;
                service.upload(filename, multiRequest);
            }
        }catch(Exception ex){
            logger.error("上传图片出错。", ex);
            return super.buildError(ex.getMessage());
        }
        return super.buildSucces();
    }

    @RequestMapping(value="/resource/img/{name}.action")
    public void view(@PathVariable("name") String name, HttpServletResponse response) throws IOException {
        //response.setContentType("image/png");
        if(name.endsWith("svg")){
            response.setContentType("image/svg+xml");
        }
        service.readImage(response.getOutputStream(), name);
    }

    @RequestMapping(value="/resource/delete.action")
    public @ResponseBody
    Object delete(@RequestBody List<String> ids) {
        service.delMore(ids);
        return super.buildSucces();
    }

    @PostMapping(value="/resource/export.action")
    public @ResponseBody
    void export(String ids, HttpServletRequest req, HttpServletResponse res) {
        try {
            JSONArray json = JSONArray.parseArray(ids);
            File f = service.export(json);

            res.setContentType("application/octet-stream");
            String contentDisposition = "attachment; filename=\"resource.zip\"";
            res.setHeader("Content-Disposition", contentDisposition);
            FileInputStream fis = new FileInputStream(f);
            IOUtils.copy(fis, res.getOutputStream());
            fis.close();
        }catch (Exception ex){
            logger.error("下载资源出错。", ex);
        }
    }
}
