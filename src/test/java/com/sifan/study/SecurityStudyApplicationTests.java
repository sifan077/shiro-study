package com.sifan.study;

import com.sifan.study.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityStudyApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }
}
