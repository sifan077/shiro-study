/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/12
 * Time: 12:30
 * Describe:
 */

package com.sifan.study.common;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;

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
