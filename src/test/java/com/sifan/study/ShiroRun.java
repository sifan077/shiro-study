/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/12
 * Time: 10:16
 * Describe:
 */

package com.sifan.study;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.junit.Test;


public class ShiroRun {
    @Test
    public void login() {
        //1.初始化获取SecurityManager
        IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
        //2.获取Subject对象
        Subject subject = SecurityUtils.getSubject();
        //3.创建Token对象，web应用用户名密码从页面传递
        AuthenticationToken token = new UsernamePasswordToken("shentu", "123");
        //4.完成登陆
        try {
            subject.login(token);
            System.out.println("登陆成功！");
            // 判断角色
            boolean hasRole1 = subject.hasRole("role1");
            System.out.println("是否拥有role1 = " + hasRole1);
            // 判断权限
            boolean permitted = subject.isPermitted("user:insert");
            System.out.println("是否有user:insert权限 = " + permitted);
            // 检查权限，如果有就通过，没有就抛异常
            subject.checkPermission("user:select");
        } catch (UnknownAccountException e) {
            e.printStackTrace();
            System.out.println("用户不存在");
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            System.out.println("密码错误");
        } catch (AuthorizationException e) {
            e.printStackTrace();
            System.out.println("权限不足");
        }
    }

    @Test
    public void shiroMd5() {
        // 密码明文
        String password = "123";
        // 使用md5加密
        Md5Hash md5Hash = new Md5Hash(password);
        System.out.println("md5加密 = " + md5Hash.toHex());
        // 带盐加密,盐就是给密码明文拼接一个字符串，然后md5加密
        Md5Hash md5Hash2 = new Md5Hash(password, "sifan");
        System.out.println("md5带盐加密 = " + md5Hash2.toHex());

        // 为了保证数据安全，可以进行多次迭代加密
        Md5Hash md5Hash3 = new Md5Hash(password, "sifan", 3);
        System.out.println("3次md5带盐加密 = " + md5Hash3.toHex());

        // 使用父类SimpleHash进行加密
        SimpleHash simpleHash = new SimpleHash("MD5", password, "sifan", 3);
        System.out.println("3次simple选择md5加密 = " + simpleHash.toHex());

    }
}
