/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.service.etl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

/**
 * 连接FTP服务类，通过FTP实现tomcat集群DFS功能
 * 上传的文件都放入FTP中，FTP的文件通过NGINX访问
 * @Author zxd
 * @Date 2020/9/13 8:17 下午
 */
@Service
public class FtpService {

    private static Logger log = LogManager.getLogger(FtpService.class);

    @Value("${dfs.ftp.host}")
    private String host;

    @Value("${dfs.ftp.port}")
    private Integer port;

    @Value("${dfs.ftp.user}")
    private String user;

    @Value("${dfs.ftp.password}")
    private String password;

    @Value("${dfs.nginx.host}")
    private String nginxHost;

    public void uploadFile(File srcFile){
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis = null;

        try {
            ftpClient.connect(host, port);
            ftpClient.login(user, password);

            fis = new FileInputStream(srcFile);
            //设置上传目录
            ftpClient.changeWorkingDirectory("/");
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");
            //设置文件类型（二进制）
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile(srcFile.getName(), fis);
        } catch (IOException e) {
            log.error("FTP客户端出错！", e);
        } finally {
            IOUtils.closeQuietly(fis);
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error("关闭FTP连接发生异常！", e);
            }
        }
    }

    /**
     * 从URL中读取文件
     * @param file
     * @param os
     */
    public void readFile(String file, OutputStream os){
        InputStream input = null;
        try {
            URL source = new URL(nginxHost  + "/" + file);
            input = source.openStream();
            IOUtils.copy(input, os);
        }catch (Exception ex){
            log.error("读取出错。", ex);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public InputStream readFile(String file){
        InputStream input = null;
        try {
            URL source = new URL(nginxHost + "/" + file);
            input = source.openStream();
        }catch (Exception ex){
            log.error("读取出错。", ex);
        }
        return input;
    }
}
