package com.example.trueque_api;

import com.trueque_api.TruequeApiApplication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TruequeApiApplication.class)
@ActiveProfiles("test")
class TruequeApiApplicationTests {
    @Test
    void contextLoads() {
    }
}