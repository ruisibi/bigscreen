/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.frame;

import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

/**
 * 组织机构，用来控制数据权限
 * @author hq
 *
 */
public class Department extends BaseEntity {
    private Integer id;//
    
    private String text;  //用在 eaayui 的 tree

    private String deptCode;//

    private String deptName;//

    private String deptNote;//

    private Integer pid;//
    
    private Boolean children;  //tree 的状态
    
    private String icon; //图标

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode == null ? null : deptCode.trim();
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName == null ? null : deptName.trim();
    }

    public String getDeptNote() {
        return deptNote;
    }

    public void setDeptNote(String deptNote) {
        this.deptNote = deptNote == null ? null : deptNote.trim();
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public Boolean getChildren() {
		return children;
	}

	public void setChildren(Boolean children) {
		this.children = children;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public void validate() {
		 this.deptCode = RSBIUtils.htmlEscape(this.deptCode);
		 this.deptName = RSBIUtils.htmlEscape(this.deptName);
		 this.deptNote = RSBIUtils.htmlEscape(this.deptNote);
	}
}
