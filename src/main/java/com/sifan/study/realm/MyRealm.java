/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/12
 * Time: 16:10
 * Describe:
 */

package com.sifan.study.realm;

import com.sifan.study.domain.User;
import com.sifan.study.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;


@Component
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    // 自定义授权方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 1. 创建对象，封装当前登陆用户的角色、权限信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        // 2. 数据库内获取数据信息
        String principal = principalCollection.getPrimaryPrincipal().toString();
        List<String> roles = userService.getUserRoleInfo(principal);
        List<String> permissions = userService.getUserPermissionInfo(roles);
        System.out.println(roles);
        System.out.println(permissions);
        // 3，存储角色
        info.addRoles(roles);
        info.addStringPermissions(permissions);

        return info;
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
