package com.edulearn;

import edulearn.com.spring_web.EduLearnApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = EduLearnApplication.class)
@ActiveProfiles("test")
class EduLearnApplicationTests {

    @Test
    void contextLoads() {
    }
}
