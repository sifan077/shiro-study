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

### 4.自定义登陆认证
#### 4.1 实现`AuthenticatingRealm`接口
自定义登陆第一步，实现`AuthenticatingRealm`接口:
```java
public class MyRealm extends AuthenticatingRealm {

    // 自定义的登陆认证方法，shiro的login方法底层会调用该类的认证方法进行认证
    // 配置自定义的realm生效,在ini内配置，或者在springboot中配置
    // 该方法只是获取进行对比的信息，认证逻辑还是shiro底层的逻辑完成

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1. 获取身份信息
        String principal = authenticationToken.getPrincipal().toString();
        // 2. 获取凭证信息
        String password = new String((char[]) authenticationToken.getCredentials());
        System.out.println("认证用户信息 " + principal + "  " + password);
        // 3. 获取数据库中存储的用户信息
        if (principal.equals("shentu")) {
            // 3.1 数据库中查询出密码
            String pwdInfo = "34e3696dfc88f4c170bb2ac4ad77a095";
            // 4. 创建封装校验逻辑对象
            AuthenticationInfo info = new SimpleAuthenticationInfo(
                    authenticationToken.getPrincipal(),
                    pwdInfo,
                    ByteSource.Util.bytes("sifan"),
                    authenticationToken.getPrincipal().toString()
            );
            // 5. 封装对象返回
            return info;
        }
        return null;
    }
}

```
#### 4.2 修改运行的配置
```ini
[main]
md5CredentialsMatcher = org.apache.shiro.authc.credential.Md5CredentialsMatcher
md5CredentialsMatcher.hashIterations = 3
myrealm=com.sifan.study.common.MyRealm
myrealm.credentialsMatcher = $md5CredentialsMatcher
securityManager.realms = $myrealm

[users]
shentu = 34e3696dfc88f4c170bb2ac4ad77a095,role1,role2

[roles]
role1 = user:insert,user:select
```
然后重新运行登陆案例。

### 5.springboot集成shiro

#### 5.1 环境搭建

1. 现在`pom.xml`内加入如下依赖:

```xml-dtd
 <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring-boot-web-starter</artifactId>
            <version>1.9.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.28</version>
        </dependency>

        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.4.1</version>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
        </dependency>
```

2. 修改springboot配置文件:

```yml
server:
  port: 8080
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/shiro
    username: root
    password: password
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
shiro:
  loginUrl: /myController/login
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true

```

#### 5.2 简单的登陆案例

##### 创建数据库

```sql
create table user
(
    id   bigint auto_increment comment '编号'
        primary key,
    name varchar(30) null comment '用户名',
    pwd  varchar(50) null comment '密码',
    rid  bigint      null comment '角色编号'
)
    comment '用户表' charset = utf8;


```

##### 使用插件生成`User`的相关类；

##### 修改serviece层，添加登陆方法；

```java
public interface UserService extends IService<User> {
    // 用户登陆
    User getUserInfoByName(String name);
}

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserInfoByName(String name) {
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getName,name);
        User user = userMapper.selectOne(lqw);
        return user;
    }
}
```

##### 集成`AuthorizingRealm`重写自己的`MyRealm`;

```java
@Component
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    // 自定义授权方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    // 自定义登陆认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 1.获取用户身份信息
        String name = authenticationToken.getPrincipal().toString();
        // 2.调用业务层获取用户信息（数据库表中存的）
        User user = userService.getUserInfoByName(name);
        // 3. 非空判断下，将数据封装返回
        if (!Objects.isNull(name)) {
            AuthenticationInfo info = new SimpleAuthenticationInfo(
                    authenticationToken.getPrincipal(),
                    user.getPwd(),
                    ByteSource.Util.bytes("sifan"),
                    authenticationToken.getPrincipal().toString()

            );
            return info;
        }
        return null;
    }
}
```

##### 编写登陆相关的html界面:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>Shiro登陆认证</h1>
<br>
<form action="/myController/userLogin">
    <div>用户名:<input type="text" name="name" value="shentu"></div>
    <div>密码:<input type="password" name="pwd" value="123"></div>
    <div><input type="submit" value="登陆"></div>
</form>
</body>
</html>
```

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>Shiro登陆认证后主页面</h1>
<br>
登陆用户为:<span th:text="${session.user}"></span>
</body>
</html>
```

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>出现错误</h1>
</body>
</html>
```

##### 实现相关Controller

```java
@Controller
@RequestMapping("myController")
public class MyController {

    // 跳转登陆界面
    @GetMapping("login")
    public String login() {
        return "login";
    }

    @GetMapping("userLogin")
    public String userLogin(String name, String pwd, HttpSession session) {
        //1 获取 Subject 对象
        Subject subject = SecurityUtils.getSubject();
        //2 封装请求数据到 token 对象中
        AuthenticationToken token = new
                UsernamePasswordToken(name, pwd);
        //3 调用 login 方法进行登录认证
        try {
            subject.login(token);
            session.setAttribute("user", token.getPrincipal().toString());
            return "main";
        } catch (AuthenticationException e) {
            return "error";
        }
    }
}
```

##### 实现`Shiro`配置

```java
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
        // 5.返回
        return securityManager;
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
        //设置需要进行登录认证的拦截范围
        definition.addPathDefinition("/**", "authc");
        return definition;
    }
}
```

#### 5.3 记住我功能

首先给`login.html`表单添加一个复选框：

```html
<form action="/myController/userLogin">
    <div>用户名:<input type="text" name="name" value="shentu"></div>
    <div>密码:<input type="password" name="pwd" value="123"></div>
    <div>记住用户: <input type="checkbox" name="rememberMe" value="true"></div>
    <div><input type="submit" value="登陆"></div>
</form>
```

修改登陆controller,多获取一个rememberMe,存入`AuthenticationToken`中:

```java
    @GetMapping("userLogin")
    public String userLogin(String name, String pwd,
                            @RequestParam(defaultValue = "false") boolean rememberMe,
                            HttpSession session) {
        //1 获取 Subject 对象
        Subject subject = SecurityUtils.getSubject();
        //2 封装请求数据到 token 对象中
        AuthenticationToken token = new
                UsernamePasswordToken(name, pwd, rememberMe);
        //3 调用 login 方法进行登录认证
        try {
            subject.login(token);
            session.setAttribute("user", token.getPrincipal().toString());
            return "main";
        } catch (AuthenticationException e) {
            return "error";
        }
    }
```

修改`ShiroConfig`的内置过滤拦截器:

```java
    //配置 Shiro 内置过滤器拦截范围
    @Bean
    public DefaultShiroFilterChainDefinition
    shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition definition = new
                DefaultShiroFilterChainDefinition();
        //设置不认证可以访问的资源
        definition.addPathDefinition("/myController/userLogin", "anon");
        definition.addPathDefinition("/myController/login", "anon");
        //设置需要进行登录认证的拦截范围
        definition.addPathDefinition("/**", "authc");
        //添加存在用户的过滤器（rememberMe）
        definition.addPathDefinition("/**", "user");
        return definition;
    }
```

增加一个测试`rememberMe`是否生效的`controller`:

```java
    // 登陆认证验证rememberMe
    @GetMapping("useLoginRM")
    public String userLogin(HttpSession session) {
        session.setAttribute("user", "rememberMe");
        return "main";
    }
```

#### 5.4 登出功能

在`main.html`中添加退出的链接:

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>Shiro登陆认证后主页面</h1>
<br>
登陆用户为:<span th:text="${session.user}"></span>
<br>
<a href="/logout">登出</a>
</body>
</html>
```

修改`ShiroConfig`的内置过滤拦截器:

```java
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
```

#### 5.5 最简单的角色验证

写一个登陆验证角色的controller:

```java
    // 登陆验证验证角色
    @RequiresRoles("admin")
    @GetMapping("/userLoginRoles")
    public String userLoginRoles() {
        System.out.println("登陆验证验证角色");
        return "验证角色成功";
    }
```

在`main.html`中添加跳转验证的链接：

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>Shiro登陆认证后主页面</h1>
<br>
登陆用户为:<span th:text="${session.user}"></span>
<br>
<a href="/logout">登出</a><br>
<a href="/userLoginRoles">测试授权</a><br>

</body>
</html>
```

修改`MyRealm`中的`doGetAuthorizationInfo`方法，添加假数据：

```java
   // 自定义授权方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 1. 创建对象，封装当前登陆用户的角色、权限信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 2. 数据库内获取数据信息

        // 3，存储角色
        info.addRole("admin");

        return info;
    }
```



