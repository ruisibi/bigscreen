/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.bigscreen;

import com.alibaba.fastjson.JSONArray;
import com.ruisitech.bi.entity.bigscreen.Resource;
import com.ruisitech.bi.mapper.bigscreen.ResourceMapper;
import com.ruisitech.bi.service.etl.FtpService;
import com.ruisitech.bi.util.RSBIUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @ClassName ResourceService
 * @Description ResourceService
 * @Author huangqin
 * @Date 2021/6/7 12:01 下午
 */
@Service
public class ResourceService {

    @Value("${dfs.ftp.use}")
    private Boolean useFtp;

    @Autowired
    private FtpService ftpService;

    @Autowired
    private ResourceMapper mapper;

    /**
     * 导出资源文件，导出为sql
     * @param json
     */
    public File export(JSONArray json) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<String> ids = new ArrayList<>();
        for(int i=0; i<json.size(); i++){
            ids.add(json.getString(i));
        }
        List<Resource> res = mapper.listByIds(ids);
        StringBuilder sb = new StringBuilder();
        for(Resource r : res){
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
        //生成文件
        List<File> files = new ArrayList<>();
        String path = System.getProperty("java.io.tmpdir");
        File resFile = new File(path+"res.sql");
        FileUtils.writeStringToFile(resFile, sb.toString(), "utf-8");
        files.add(resFile);
        //读取图片
        for(Resource r : res){
            File f = new File(path + r.getPath());
            FileOutputStream fos = new FileOutputStream(f);
            this.readImage(fos, r.getPath());
            fos.close();
            files.add(f);
        }
        //压缩文件
        File dsetFile = new File(path + "resource.zip");
        zip(dsetFile, files);
        return dsetFile;
    }

    public void zip(File destFile, List<File> srcFiles) throws Exception {

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destFile))) {

            for (File file : srcFiles) {
                if (file == null || !file.exists()) {
                    continue;
                }

                recursionCompress(zos, file, file.getName());
            }

        } catch (Exception e) {
            throw e;
        }
    }

    private static void recursionCompress(ZipOutputStream zos, File srcFile, String zipEntryName) throws Exception {
        // 如果是目录，则创建目录并递归压缩子文件
        if (srcFile.isDirectory()) {
            // 添加目录
            zos.putNextEntry(new ZipEntry(zipEntryName + File.separator));
            zos.closeEntry();

            // 遍历并压缩子文件
            for (File subFile : srcFile.listFiles()) {
                String subZipEntryName = zipEntryName + File.separator + subFile.getName();
                recursionCompress(zos, subFile, subZipEntryName);
            }

            return;
        }

        // 如果是文件，则压缩文件
        zos.putNextEntry(new ZipEntry(zipEntryName));
        try (FileInputStream inputStream = new FileInputStream(srcFile)) {
            int len;
            byte[] buf = new byte[2 * 1024];

            while ((len = inputStream.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
        } catch (Exception e) {
            throw e;
        }
        zos.closeEntry();
    }


    @Transactional(rollbackFor = Exception.class)
    public void delMore(List<String> ids){
        for(String id : ids){
            this.deleteByPrimaryKey(id);
        }
    }

    public int deleteByPrimaryKey(String id){
        Resource r = mapper.selectByPrimaryKey(id);
        //删除文件
        String path = RSBIUtils.getUploadFilePath();
        File f = new File(path + r.getPath());
        if(f.exists()){
            f.delete();
        }
        return mapper.deleteByPrimaryKey(id);
    }

    public int insertSelective(Resource record){
        return mapper.insertSelective(record);
    }

    public Resource selectByPrimaryKey(String id){
        return mapper.selectByPrimaryKey(id);
    }

    public List<Resource> list(){
        Integer userId = null;
        return mapper.list(userId);
    }

    /**
     * 图片上传
     * @param multiRequest
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    public void upload(String name, MultipartHttpServletRequest multiRequest) throws IllegalStateException, IOException{
        Iterator<String> iter=multiRequest.getFileNames();
        String picPath = "";
        while(iter.hasNext()){
            //一次遍历所有文件
            MultipartFile file=multiRequest.getFile(iter.next());
            if(file!=null) {
                String filename = file.getOriginalFilename();

                String extName = filename.substring(filename.lastIndexOf("."));
                if(!(extName.equalsIgnoreCase(".gif") || extName.equalsIgnoreCase(".png") || extName.equalsIgnoreCase(".jpg") || extName.equalsIgnoreCase(".svg"))){
                    throw new RuntimeException("只支持gif/jpg/png/svg格式图片。");
                }
                long size = file.getSize();
                if(size > 1024 * 1024){
                    throw new RuntimeException("图片大小超过1MB。");
                }
                //拷贝文件
                String id = UUID.randomUUID().toString().replace("-", "");
                filename = id + extName;
                String path = RSBIUtils.getUploadFilePath();
                String rpath =  path + "resource";  //资源文件放在 resource 目录下
                File rfile = new File(rpath);
                if(!rfile.exists()){
                    rfile.mkdirs();
                }
                File targetFile = new File(rpath + "/" + filename);
                file.transferTo(targetFile);
                //是否传FTP
                if(useFtp){
                    ftpService.uploadFile(targetFile);
                }
                picPath = filename;
            }
        }
        //入库
        Resource r = new Resource();
        r.setCreateUser(RSBIUtils.getLoginUserInfo().getUserId());
        r.setName(name);
        r.setPath(picPath);
        r.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        mapper.insertSelective(r);
    }

    public void readImage(OutputStream os, String pic){
        try {
            String path = RSBIUtils.getUploadFilePath();
            pic = "resource/" + pic; //资源文件都放在 resource 目录下
            if (useFtp) {
                ftpService.readFile(pic, os);
            } else {
                InputStream is = null;
                try {
                    File f = new File(path +  pic);
                    if (!f.exists()) {
                        return;
                    }
                    is = new FileInputStream(f);
                    IOUtils.copy(is, os);
                } finally {
                    if (is != null) is.close();
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public List<Resource> listByPaths(List<String> paths){
        return mapper.listByPaths(paths);
    }
}
