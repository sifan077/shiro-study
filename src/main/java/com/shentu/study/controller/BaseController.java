/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/10/11
 * Time: 21:47
 * Describe:
 */

package com.shentu.study.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {
    @GetMapping("/")
    public String index() {
        return "<h1>index</h1>";
    }
}
