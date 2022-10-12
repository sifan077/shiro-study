/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/11
 * Time: 21:47
 * Describe:
 */

package com.sifan.study.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    @GetMapping("/")
    public String index() {
        return "<h1>index</h1>";
    }

    // 登陆验证验证角色
    @RequiresRoles("admin")
    @GetMapping("/userLoginRoles")
    public String userLoginRoles() {
        return "验证角色成功";
    }

    // 登陆验证验证权限
    @RequiresPermissions("user:delete")
    @GetMapping("/userPermissions")
    public String userPermissions() {
        return "验证权限成功";
    }
}
