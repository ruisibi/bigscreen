/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.bigscreen;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruisitech.bi.entity.common.BaseEntity;

import java.util.Date;

/**
 * 仪表盘/大屏资源文件
 * @ClassName Resource
 * @Description Resource
 * @Author huangqin
 * @Date 2021/6/7 11:50 上午
 */
public class Resource extends BaseEntity {
    private String id;
    private String name;
    private String path;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createDate;
    private Integer createUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }
}
