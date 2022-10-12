# Shiro学习
# 1. 环境搭建步骤
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
shentu=123
sifan=456
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
      }catch (Exception e){
         System.out.println("登陆失败！");
         e.printStackTrace();
      }
   }
}
```

   