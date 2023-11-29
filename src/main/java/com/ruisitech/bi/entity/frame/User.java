/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.entity.frame;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruisitech.bi.entity.common.BaseEntity;
import com.ruisitech.bi.util.RSBIUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public final class User extends BaseEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 6096757156465671644L;

	private Integer userId;
	private String staffId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date loginDate;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date regDate;
	private Integer logCnt;
	private String loginName;
	@JsonIgnore
	private String password;
	private String gender;
	private String mobilePhone;
	private String email;
	private String officeTel;
	private Integer state; //1 为启用， 0为停用。
	private String channel; //app 用户的设备ID
	private Integer deptId;
	private String deptCode;  //部门用来数据过滤的值
	private String deptName;
	@JsonIgnore
	private Date errDate; //输入密码错误时间
	private String wxid; //用户对应的微信Id
	private String logIp; //ip地址

	@JsonIgnore
	private List<String> urls; //能访问的URL

	public User() {

	}
	public String getStaffId() {
		return staffId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getOfficeTel() {
		return officeTel;
	}
	public void setOfficeTel(String officeTel) {
		this.officeTel = officeTel;
	}

	@Override
	public String toString() {
		return "id = " + this.userId + ", name = " + this.loginName;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getLogCnt() {
		return logCnt;
	}
	public void setLogCnt(Integer logCnt) {
		this.logCnt = logCnt;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Integer getDeptId() {
		return deptId;
	}
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public void validate(){
		this.loginName = RSBIUtils.htmlEscape(this.loginName);
		this.staffId = RSBIUtils.htmlEscape(this.staffId);
	}
	public Date getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Date getRegDate() {
		return regDate;
	}
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	public Date getErrDate() {
		return errDate;
	}
	public void setErrDate(Date errDate) {
		this.errDate = errDate;
	}
	public String getWxid() {
		return wxid;
	}
	public void setWxid(String wxid) {
		this.wxid = wxid;
	}
	public String getLogIp() {
		return logIp;
	}
	public void setLogIp(String logIp) {
		this.logIp = logIp;
	}

	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
}
