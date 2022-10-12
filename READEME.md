# Shiro学习

### 1. 环境搭建步骤

1. 创建一个基础的springboot项目;
2. 引入如下相关依赖
    ```xml
     <dependencies>
        <dependency>
            <groupId>org.apache.shiro</groupId>
                <artifactId>shiro-core</artifactId>
            <version>1.9.0</version>
        </dependency>
        <dependency>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
            <version>1.2</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
            <version>4.12</version>
        </dependency>
    </dependencies>
    ```
3. 最简单的登陆案例

在资源文件夹下创建一个`shiro.ini`文件，做出如下配置：

```ini
[users]
shentu = 123
sifan = 456
 ```

测试登陆代码如下:

```java
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
        AuthenticationToken token = new UsernamePasswordToken("shentu", "456");
        //4.完成登陆
        try {
            subject.login(token);
            System.out.println("登陆成功！");
        } catch (Exception e) {
            System.out.println("登陆失败！");
            e.printStackTrace();
        }
    }
}
```

### 2.角色权限相关api使用

#### 2.1 修改ini文件

修改ini下的文件如下:

```ini
[users]
shentu = 123,role1,role2

[roles]
role1 = user:insert,user:select
```

#### 2.2 角色判断和权限

```java
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
}
```

### 3. 加密相关

简单加密相关的api使用如下:

```java
public class ShiroRun {
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

```


   