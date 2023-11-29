/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.dao.DaoHelper;
import com.ruisitech.bi.entity.bigscreen.BigScreen;
import com.ruisitech.bi.entity.common.PageParam;
import com.ruisitech.bi.mapper.bigscreen.BigScreenMapper;
import com.ruisitech.bi.service.bigscreen.ResourceService;
import com.ruisitech.bi.service.etl.FtpService;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 模版下载服务类
 * @ClassName DownloadService
 * @Description DownloadService
 * @Author gdp
 * @Date 2023/7/18 10:40 上午
 */
@Service
public class DownloadService {

    @Value("${rsbi.templateUrl}")
    private String templateUrl;

    @Value("${rsbi.templateToken}")
    private String templateToken;

    @Value("${rsbi.provideTemplateService}")
    private Boolean provideTemplateService;

    @Autowired
    private RestTemplate restTemplate;

    @Resource
    private BigScreenMapper mapper;

    @Autowired
    private ResourceService resourceService;

    public String getTemplateUrl(){
        return templateUrl;
    }

    @Value("${dfs.ftp.use}")
    private Boolean useFtp;

    @Autowired
    private FtpService ftpService;

    @Autowired
    private DaoHelper sysDaoHelper;


    /**
     * 获取远程服务器的大屏模版列表
     */
    public List<BigScreen> queryTemplates(PageParam pageParam){
        if(templateUrl == null || templateUrl.length() == 0){
            throw new RuntimeException("未配置模版服务器的地址");
        }
        int page = pageParam.getPage();
        int rows = pageParam.getRows();
        String url = templateUrl + "template/remote/list.action?token="+templateToken+"&page="+page+"&rows="+rows;
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = JSON.parseObject(response);
        int result = json.getInteger("result");
        if(result == 1) {
            JSONArray ls = json.getJSONArray("rows");
            int total = json.getInteger("total");
            pageParam.setTotal(total);
            List<BigScreen> ret = new ArrayList<>();
            for (int i = 0; i < ls.size(); i++) {
                ret.add(JSONObject.toJavaObject(ls.getJSONObject(i), BigScreen.class));
            }
            return ret;
        }else{
            throw new RuntimeException(json.getString("msg"));
        }
    }

    public List<BigScreen> list(String token){
        if(!templateToken.equals(token)){
            throw new RuntimeException("链接模版服务器token异常");
        }
        if(!provideTemplateService){
            throw new RuntimeException("服务器未提供模型下载功能");
        }
        return mapper.queryTemplate();
    }

    public void download(Integer id) throws IOException {
        if(templateUrl == null || templateUrl.length() == 0){
            throw new RuntimeException("未配置模版服务器的地址");
        }
        String url = templateUrl + "template/remote/getTemplate.action?token="+this.templateToken+"&id="+id;
        String response = restTemplate.getForObject(url, String.class);
        JSONObject json = JSON.parseObject(response);
        int result = json.getInteger("result");
        if(result == 1) {
            JSONObject dt = json.getJSONObject("rows");
            BigScreen bs = JSONObject.toJavaObject(dt, BigScreen.class);

            //插入资源表
            String sqls = bs.getEditorEnable();
            if(sqls != null && sqls.length() > 0 ) {
                sysDaoHelper.execute(sqls);
            }

            //获取资源图片
            JSONArray imgs = JSONArray.parseArray(bs.getImages());
            this.downloadImages(imgs);

            //插入本地库中
            bs.setUserId(RSBIUtils.getLoginUserInfo().getUserId());
            bs.setCreatedate(new Date());
            bs.setUpdatedate(new Date());
            bs.setId(mapper.maxBigScreenId());
            JSONObject cfg = JSONObject.parseObject(bs.getPageInfo());
            cfg.put("id", bs.getId());
            bs.setPageInfo(JSONObject.toJSONString(cfg));
            bs.setPageName("模版-" + bs.getPageName());
            bs.setEditorEnable(null);
            mapper.insertSelective(bs);
        }else{
            throw new RuntimeException(json.getString("msg"));
        }

    }

    /**
     * 下载图像资源
     * @param imgs
     */
    private void downloadImages(JSONArray imgs) throws IOException {
        if(imgs == null || imgs.size() == 0){
            return;
        }
        for (int i=0; i<imgs.size(); i++){
            String img = imgs.getString(i);
            String url = templateUrl + "template/remote/getImage.action?name="+img+"&token="+this.templateToken;
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);
            byte[] body = responseEntity.getBody();
            String path = RSBIUtils.getUploadFilePath();
            String rpath =  path + "resource";  //资源文件放在 resource 目录下
            File f = new File(rpath+"/" + img);
            FileUtils.writeByteArrayToFile(f, body);
            //是否传FTP
            if(useFtp){
                ftpService.uploadFile(f);
            }
        }
    }

    public BigScreen getTemplate(Integer id, String token){
        if(!templateToken.equals(token)){
            throw new RuntimeException("链接模版服务器token异常");
        }
        if(!provideTemplateService){
            throw new RuntimeException("服务器未提供模型下载功能");
        }
        BigScreen bs = mapper.selectByPrimaryKey(id);
        //解析图片
        JSONObject cfg = JSONObject.parseObject(bs.getPageInfo());
        List<String> imgs = new ArrayList<>();
        this.parseJson(cfg, imgs);
        bs.setImages(JSONArray.toJSONString(imgs));
        //解析resource表
        String sqls = this.parseResource(imgs);
        bs.setEditorEnable(sqls); //把sqls临时放入 EditorEnable 字段
        return bs;
    }

    /**
     * 遍历json的所有value，并找出里面的图片资源
     * @param object
     */
    public void parseJson(Object object, List<String> imgs) {
        if(object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            for (Map.Entry<String, Object> entry: jsonObject.entrySet()) {
                Object o = entry.getValue();
                if(o instanceof String) {
                    String val = (String) entry.getValue();
                   if(val.startsWith("resource/img")){  //说明是图片资源
                       String path = this.getImagePath(val);
                       if(!imgs.contains(path)){
                           imgs.add(path);
                       }
                   }
                } else {
                    parseJson(o, imgs);
                }
            }
        }
        if(object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for(int i = 0; i < jsonArray.size(); i ++) {
                parseJson(jsonArray.get(i), imgs);
            }
        }
        if(object instanceof String || object instanceof Integer) {
            //System.out.println(object.toString());
        }
    }

    private String getImagePath(String value){
        value = value.replaceFirst("resource/img/", "");
        value = value.replaceFirst(".action", "");
        return value;
    }

    public void getImage(OutputStream os, String pic, String token){
        if(!templateToken.equals(token)){
            throw new RuntimeException("链接模版服务器token异常");
        }
        if(!provideTemplateService){
            throw new RuntimeException("服务器未提供模型下载功能");
        }
        resourceService.readImage(os, pic);
    }

    public String parseResource(List<String> imgs){
        if(imgs.size() == 0){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        List<com.ruisitech.bi.entity.bigscreen.Resource> ls = resourceService.listByPaths(imgs);
        //创建 delete
        for(com.ruisitech.bi.entity.bigscreen.Resource r : ls){
            sb.append("delete from sc_resource where id = '"+r.getId()+"';\n");
        }
        //创建insert
        for(com.ruisitech.bi.entity.bigscreen.Resource r : ls){
            sb.append("insert into sc_resource(id, name, path, create_date, create_user) values(");
            sb.append("'");
            sb.append(r.getId());
            sb.append("'");
            sb.append(",");
            sb.append("'");
            sb.append(r.getName());
            sb.append("'");
            sb.append(",");
            sb.append("'");
            sb.append(r.getPath());
            sb.append("'");
            sb.append(",");
            sb.append("'");
            sb.append(sdf.format(r.getCreateDate()));
            sb.append("'");
            sb.append(",");
            sb.append(r.getCreateUser());
            sb.append(");");
            sb.append("\n");
        }
        return sb.toString();
    }

}
