package org.projeto.snowflakekafkapipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling          // ← esta anotação precisa estar aqui
@EnableConfigurationProperties
public class SnowflakeKafkaPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnowflakeKafkaPipelineApplication.class, args);
    }
}
