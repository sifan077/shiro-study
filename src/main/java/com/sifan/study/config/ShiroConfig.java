/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/12
 * Time: 16:17
 * Describe:
 */

package com.sifan.study.config;

import com.sifan.study.realm.MyRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

    @Autowired
    private MyRealm myRealm;

    // 配置securityManager
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        // 1. 创建 DefaultWebSecurityManager 对象
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 2. 创建加密对象，设置相关属性
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        // 2.1 使用用md5加密
        matcher.setHashAlgorithmName("MD5");
        // 2.2 迭代加密的次数
        matcher.setHashIterations(3);
        // 3. 将加密对象存储到myRealm中
        myRealm.setCredentialsMatcher(matcher);
        // 4. 将myRealm存入 DefaultWebSecurityManager 对象对象中
        securityManager.setRealm(myRealm);

        // 4.1 设置rememberMe
        securityManager.setRememberMeManager(rememberMeManager());

        // 5.返回
        return securityManager;
    }

    //cookie 属性设置
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        //设置跨域
        //cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(30 * 24 * 60 * 60);
        return cookie;
    }

    //创建 Shiro 的 cookie 管理对象
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new
                CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());

        cookieRememberMeManager.setCipherKey("1234567890987654".getBytes());
        return cookieRememberMeManager;
    }

    //配置 Shiro 内置过滤器拦截范围
    @Bean
    public DefaultShiroFilterChainDefinition
    shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition definition = new
                DefaultShiroFilterChainDefinition();
        //设置不认证可以访问的资源
        definition.addPathDefinition("/myController/userLogin", "anon");
        definition.addPathDefinition("/myController/login", "anon");
        //配置登出过滤器
        definition.addPathDefinition("/logout", "logout");
        //设置需要进行登录认证的拦截范围
        definition.addPathDefinition("/**", "authc");
        //添加存在用户的过滤器（rememberMe）
        definition.addPathDefinition("/**", "user");
        return definition;
    }

}
