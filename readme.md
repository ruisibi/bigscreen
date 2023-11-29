#睿思BI数据大屏系统 <br/>
睿思BI数据大屏系统是由成都睿思商智科技有限公司自主研发的，具有自主知识产权的BI数据大屏系统，帮助客户快捷、灵活、零代码创建数据大屏应用，也可做系统开发的低代码平台。<br>
前端使用VUE，在 web-vue 目录中，使用 npm install 安装库，然后 npm run dev 启动项目。<br/>
后端使用Springboot, 执行 RsbiEntApplication 的 main 函数启动。 <br/>
数据库采用mysql，后端脚本在 datas 目录下。 <br/>

##数据还原
1.在datas目录中找到 rsbi_bigscreen.sql文件，此文件是系统支撑库备份文件。<br/>
2.进入MYSQL建立 rsbi_bigscreen 数据库及 rsbi_bigscreen_dw 数据库，其中 rsbi_bigscreen 为支撑库，rsbi_bigscreen_dw 是数据仓库。<br/>

`mysql -u root -p                       //在命令提示符（CMD）里进入MySQL
 create database rsbi_bigscreen          //创建rsbi支撑库
 create database rsbi_bigscreen_dw       //创建 rsbi 数据仓库`
 
3.在命令提示符（CMD）里用mysql命令还原rsbi_bigscreen.sql数据到数据库中，相关命令为：<br/>
 
`mysql –uroot –pxxxxxx rsbi_bigscreen<G:\datas\rsbi_bigscreen.sql
`
请注意sql文件路径，其中xxxxxx代表你的数据库root账号的密码。 <br/>

##安装Redis
下载Redis并安装成功即可。 <br/>

##修改配置文件 application.yml
1.修改数据库链接的 password 内容，既你数据库账户root的密码，如果您的MYSQL root密码是123456，则不用修改。<br/>
`datasource:
     type: com.alibaba.druid.pool.DruidDataSource
     dbType: mysql #定义支撑库类型
     dwType: mysql #定义数据仓库类型
     druid:
       master:  # 支持库
         url: jdbc:mysql://localhost:3306/rsbi_bigscreen?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8
         username: root
         password: 12345678
         driver-class-name: com.mysql.cj.jdbc.Driver
         initialSize: 5
         minIdle: 10
         maxActive: 50
         maxWait: 60000
       datawarehouse:  #数据仓
         url: jdbc:mysql://localhost:3306/rsbi_bigscreen_dw?useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=GMT%2B8
         username: root
         password: 12345678
         driver-class-name: com.mysql.cj.jdbc.Driver
         initialSize: 5
         minIdle: 10
         maxActive: 50
         maxWait: 60000`
2.修改Redis配置，如下：
`redis:
     port: 6379
     database: 1
     host: localhost
     password:
     jedis:
       pool:
         max-active: 100
         max-wait: 1000ms
         max-idle: 200
         min-idle: 0
     timeout: 5000ms
     `
3.修改上传文件路径,如果不设置，系统文件默认存项目的files目录中。
 ` upFilePath: #上传文件路径`
 
4.配置DFS，采用ftp实现，配置如下：
`dfs:
   ftp:
     use: false
     host: 192.168.1.7
     port: 21
     user: hq
     password: 123456
   nginx:
     #nginx访问ftp文件的IP
     host: http://192.168.1.7`

##通过RsbiEntApplication类启动项目
在项目中找到RsbiEntApplication类并启动，如果启动成功说明后台配置完成。

##启动前端项目
1.进入web-vue 目录 <br/>
2.执行 npm install 安装项目 <br/>
3.执行 npm run dev 启动前端项目 <br/>

##系统文档
1.系统介绍录像
https://www.ruisitech.com/files/bigscreen.mov
<br/>
2.文档地址(旗舰版)
http://book.ruisitech.com/ultimate/

##系统截图
系统首页<br/>
![首页](http://www.ruisitech.com/img/bs_index.jpg?v4)  <br/>

大屏设计<br/>
![大屏设计](http://www.ruisitech.com/img/bs_index2.jpg?v4)  <br/>

大屏效果1<br/>
![大屏效果1](http://www.ruisitech.com/img/bs_index3.jpg?v4)  <br/>

大屏效果2<br/>
![大屏效果2](http://www.ruisitech.com/img/bs_index4.jpg?v4)  <br/>
