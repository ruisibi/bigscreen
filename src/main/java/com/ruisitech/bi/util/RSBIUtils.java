/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import com.alibaba.fastjson.JSONObject;
import com.rsbi.ext.engine.view.context.ExtContext;
import com.ruisitech.bi.entity.frame.User;
import com.ruisitech.bi.service.frame.ShiroDbRealm;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.rsbi.ext.engine.control.InputOptionFactory.injStra;

public final class RSBIUtils {

	/**
	 * 此处存放 servletContext,方便获取
	 */
	private static ServletContext servletContext;

	public static void setServletContext(ServletContext sctx){
		servletContext = sctx;
	}

	/**
	 * 给str加密md5。
	 * @return
	 */
	public static String getEncodedStr(String str){
		if(str==null)return null;
		return getMD5(str.getBytes());
	}

	/**
	 * 获取字符串MD5
	 * @param source
	 * @return
	 */
	public static String getMD5(byte[] source) {
	    String s = null;
	    char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	            'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符
	    try {
	        java.security.MessageDigest md = java.security.MessageDigest
	                .getInstance("MD5");
	        md.update(source);
	        byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，
	        // 用字节表示就是 16 个字节
	        char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16
	        // 进制需要 32 个字符
	        int k = 0;// 表示转换结果中对应的字符位置
	        for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16
	            // 进制字符的转换
	            byte byte0 = tmp[i];// 取第 i 个字节
	            str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>
	            // 为逻辑右移，将符号位一起右移
	            str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换

	        }
	        s = new String(str);// 换后的结果转换为字符串

	    } catch (NoSuchAlgorithmException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return s;
	}

	/**
	 * 获取ext-config中配置的变量。
	 * @return
	 */
	public static String getConstant(String name){
		return ExtContext.getInstance().getConstant(name);
	}
	public static String getUUIDStr(){
		return UUID.randomUUID().toString().replace("-","");
	}

	/**
	 * 获取数据ETL的批次号
	 * @param prefix
	 * @return
	 */
	public static String getProductBatch(String prefix){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return prefix + sdf.format(new Date());
	}

	public static String dealStringParam(String vals){
		String[] vls = vals.split(",");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<vls.length; i++){
			String v = vls[i];
			sb.append("'" + v + "'");
			if(i != vls.length - 1){
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static String dealStringParam(List<String> vls, boolean isString){
		if(vls == null){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<vls.size(); i++){
			String v = vls.get(i);
			if(isString) {
				sb.append("'");
			}
			sb.append(v);
			if(isString) {
				sb.append("'");
			}
			if(i != vls.size() - 1){
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public static Map<String, String> getAllParams(HttpServletRequest req){
		Map<String, String> dt = new HashMap<String, String>();
		Enumeration<String> enu = req.getParameterNames();
		while(enu.hasMoreElements()){
			String key = (String)enu.nextElement();
			dt.put(key, req.getParameter(key));
		}
		return dt;
	}

	public static boolean isShowMenu(String name, HttpServletRequest req){
		JSONObject obj = (JSONObject)req.getAttribute("menuDisp");
		if(obj == null){
			return true;
		}
		Object r = obj.get(name);
		if(r == null){
			return true;
		}
		if(r instanceof Integer && (Integer)r == 0){
			return false;
		}else{
			return true;
		}
	}

	public static User getLoginUserInfo(){
		Subject us = SecurityUtils.getSubject();
		User u = (User)us.getSession().getAttribute(ShiroDbRealm.SESSION_USER_KEY);
		return u;
	}

	/**
	 * 生成导出html
	 * @param body
	 * @param host
	 * @param type 表示使用的类型，是 olap表示多维分析的导出， report 表示报表的导出
	 * @return
	 */
	public static String htmlPage(String body, String host, String type){
		StringBuffer sb = new StringBuffer();

		sb.append("<!DOCTYPE html>");
		sb.append("<html lang=\"en\">");
		sb.append("<head>");
		sb.append("<title>"+ RSBIUtils.getConstant("sys.productName")+"</title>");
		sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
		sb.append("<script type=\"text/javascript\" src=\""+host+"/ext-res/js/jquery.min.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\""+host+"/ext-res/js/echarts.min.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\""+host+"/resource/js/echartsUtils.js\"></script>");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+host+"/ext-res/css/bootstrap.min.css\" />");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+host+"/resource/css/font-awesome.css?v=4.4.0\" />");
		sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+host+"/resource/css/exportstyle.css\" />");
		sb.append("</head>");
		sb.append("<body class=\"gray-bg\">");

		sb.append(body);

		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	/**
	 * 从 URL 读取文件内容
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String readFileFromUrl(String url) throws IOException {
		URL source = new URL(url);
		InputStream input = source.openStream();
		String rt = null;
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				IOUtils.copy(input, output);
				rt = new String(output.toByteArray());
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
		return rt;
	}

	/**
	 * 生成仪表盘的导出文件
	 * @param body
	 * @param host
	 * @return
	 */
	public static String dashboardHtmlPage(String body, String title, String host, String styleType){
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<!DOCTYPE html>");
			sb.append("<html lang=\"en\">");
			sb.append("<head>");
			sb.append("<title>" + RSBIUtils.getConstant("sys.productName") + " - 仪表盘</title>");
			sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">");
			sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
			sb.append("<script type=\"text/javascript\" src=\""+host+"/ext-res/js/jquery.min.js\"></script>");
			sb.append("<script type=\"text/javascript\" src=\""+host+"/ext-res/js/echarts.min.js\"></script>");
			sb.append("<script type=\"text/javascript\" src=\""+host+"/resource/js/echartsUtils.js\"></script>");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+host+"/ext-res/css/bootstrap.min.css\" />");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+host+"/resource/css/font-awesome.css?v=4.4.0\" />");
			sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""+host+"/resource/css/exportstyle.css\" />");
			sb.append("</head>");
			String color = styleType != null && styleType.equals("bigscreen") ? "#0d1b3e" : "#ffffff";
			sb.append("<body class=\"gray-bg\" style=\"background-color:" + color + "\">");

			sb.append(body);

			sb.append("</body>");
			sb.append("</html>");

			return sb.toString();
		}catch (Exception ex){
			ex.printStackTrace();
			return "导出错误：" + ex.getMessage();
		}
	}

	public static boolean exist(String id, String[] ids){
		boolean exist = false;
		for(String tid : ids){
			if(tid.equals(id)){
				exist = true;
				break;
			}
		}
		return exist;
	}

	public static String getUploadFilePath() {
		String upFilePath = RSBIUtils.getConstant("upFilePath"); //读取地址
		if(upFilePath == null || upFilePath.length() == 0){ //没读取到，获取默认地址
			String path = servletContext.getRealPath("/") + "/files/";
			File fpath = new File(path);
			if(!fpath.exists()){
				fpath.mkdirs();
			}
			upFilePath = path;
		}
		return upFilePath;
	}


	/**
	 * 将容易引起xss漏洞的半角字符直接替换成全角字符 在保证不删除数据的情况下保存
	 * @param value
	 * @return
	 */
	public static String htmlEscape(String value){
		if(value == null || value.length() == 0){
			return value;
		}
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
	}

	/**
	 * Escape 编码
	 * @param src
	 * @return
	 */
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

	 /**
     * Escape解码
     * @param src 加盐字符串
     * @return 明文
     */
    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

	/**
	 * SQL 注入检测
	 * @param inputString
	 * @return
	 */
	public static String processSqlStr(String inputString)
	{

		for (int i = 0; i < injStra.length ; i++ )
		{
			String key = injStra[i];
			int pos = inputString.indexOf(key);
			if (pos >= 0)
			{
				if(key.equals("or")){  //or 特殊处理 如果 or 的后面是空格，就说明他是sql注入关键字
					if(key.equals(inputString)){
						return null;
					}
					if(inputString.length() < pos + 3){
						return null;
					}
					String right = inputString.substring(pos + 2, pos + 3);
					if(right.equals(" ")){
						return key;
					}
				}else {
					return key;
				}
			}
		}
		return null;
	}

	public static void processSql(String input){
		if(input != null  && input.length() > 0 ){
			String ret = RSBIUtils.processSqlStr(input.toLowerCase());
			if(ret != null){
				throw new RuntimeException("有SQL注入关键字" + ret);
			}
		}
	}

    public static Map<String, Object> getCfg(ServletContext servletContext){
		if(servletContext == null){
			return null;
		}
    	Map<String, Object> ret = (Map<String, Object>)servletContext.getAttribute("lic.cfg");
    	return ret;
    }

    public static String[] list2Array(List<String> ls) {
    	String[] ret = new String[ls.size()];
    	for(int i=0; i<ls.size(); i++) {
    		ret[i] = ls.get(i);
    	}
    	return ret;
    }

    public static String getDbDateFunction(String dbName){
		if("mysql".equals(dbName)){
			return "now()";
		}else if("oracle".equals(dbName)){
			return "sysdate";
		}else if("sqlser".equals(dbName)){
			return "getdate()";
		}else if("db2".equals(dbName)){
			return "current timestamp";
		}else if("psql".equals(dbName)){
			return "current_timestamp";
		}else if("dm".equals(dbName)){
			return "sysdate()";
		}else if("clickhouse".equals(dbName)){
			return "now()";
		}
		throw new RuntimeException("类型不支持");
	}
}
