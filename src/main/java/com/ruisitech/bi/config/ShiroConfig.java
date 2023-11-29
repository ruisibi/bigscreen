/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.config;

import com.ruisitech.bi.service.frame.*;
import com.ruisitech.bi.service.report.ShareUrlService;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName ShiroConfig
 * @Description ShiroConfig
 * @Author gdp
 * @Date 2020/9/18 10:29 上午
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private UserService userService;


    @Autowired
    private ShareUrlService shareUrlService;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器
        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", new SessionAuthcFilter(userService));
        filters.put("shareAuthc", new ShareUrlSessionAuthcFilter(shareUrlService));
        shiroFilterFactoryBean.setFilters(filters);
        //注销
        shiroFilterFactoryBean.setLoginUrl("/frame/Logout2.action");
        //过滤的URL
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/login/**", "anon");
        filterChainDefinitionMap.put("/chartjson/**", "anon");  //json文件不用登录
        filterChainDefinitionMap.put("/mock/**", "anon");  //mock 文件不用登录
        filterChainDefinitionMap.put("/resource/**", "anon");  //资源文件
        filterChainDefinitionMap.put("/ext-res/**", "anon");  //资源文件
        filterChainDefinitionMap.put("/", "anon");
        //filterChainDefinitionMap.put("/control/extControl", "anon");
        filterChainDefinitionMap.put("/dataware/tableApi/Gateway.action", "anon"); //数据接口访问网关
        filterChainDefinitionMap.put("/bigscreen/resource/img/**", "anon");  //上传的图片资源不拦截
        filterChainDefinitionMap.put("/mxgraph/**", "anon");  //mxgraph组件资源文件
        filterChainDefinitionMap.put("/static/**", "anon");  //资源文件(vue)
        filterChainDefinitionMap.put("/index.html", "anon");  //资源文件(vue)
        filterChainDefinitionMap.put("/rs_favicon.ico", "anon");  //资源文件(vue)
        filterChainDefinitionMap.put("/bigscreen/weather/get.action", "anon");  //天气接口，不拦截
        filterChainDefinitionMap.put("/template/remote/**", "anon");  //模版管理暴露给外部调用的接口，不拦截
        filterChainDefinitionMap.put("/report/share/**", "shareAuthc");  //报表分享后的URL拦截器
        filterChainDefinitionMap.put("/portal/share/**", "shareAuthc");  //仪表盘分享后的URL拦截器
        filterChainDefinitionMap.put("/bireport/share/**", "shareAuthc");  //OLAP报表分享后的URL拦截器
        filterChainDefinitionMap.put("/dashboard/share/**", "shareAuthc");  //仪表盘分享后的URL拦截器
        filterChainDefinitionMap.put("/etl/share/**", "shareAuthc");  //仪表盘参数分享后的URL拦截器
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(ShiroDbRealm customRealm, CacheManager cacheManager, SessionManager sessionManager, RememberMeManager rememberMeManager) {
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        defaultSecurityManager.setRealm(customRealm);
        defaultSecurityManager.setSessionManager(sessionManager);
        defaultSecurityManager.setCacheManager(cacheManager);
        defaultSecurityManager.setRememberMeManager(rememberMeManager);
        return defaultSecurityManager;
    }

    @Bean
    public ShiroDbRealm customRealm() {
        return new ShiroDbRealm();
    }

    @Bean
    public SessionDao sessionDao(){
        return new SessionDao();
    }

    @Bean
    public CacheManager cacheManager(){
        return new CustomCacheManager();
    }

    @Bean
    public SessionManager sessionManager(SessionDao sessionDao){
        DefaultWebSessionManager sm = new DefaultWebSessionManager();
        sm.setSessionDAO(sessionDao);
        sm.setSessionIdCookie(new SimpleCookie("RSBISESSIONID"));
        sm.setGlobalSessionTimeout(1800000); //设置全局会话超时时间，默认30分钟(1800000)
        sm.setDeleteInvalidSessions(true); //是否在会话过期后会调用SessionDAO的delete方法删除会话
        return sm;
    }

    @Bean
    public RememberMeManager rememberMeManager(){
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        SimpleCookie cookie = new SimpleCookie("remember");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(2592000); // 默认记住30天（单位：秒）
        rememberMeManager.setCookie(cookie);
        rememberMeManager.setCipherKey(getCipherKey());
        return rememberMeManager;
    }

    private byte[] getCipherKey(){
        KeyGenerator kg;
        try{
            kg = KeyGenerator.getInstance("AES");

        }catch (Exception ex){
            throw new RuntimeException("jiam");
        }
        kg.init(128);
        SecretKey sk = kg.generateKey();
        return sk.getEncoded();
    }

}
