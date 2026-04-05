package edulearn.com.spring_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.edulearn")
public class EduLearnApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduLearnApplication.class, args);
    }
}

