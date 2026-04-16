package org.projeto.snowflakekafkapipeline.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // Lê os nomes dos tópicos do application.properties
    @Value("${pipeline.topics.tabela1}")
    private String topicTabela1;

    @Value("${pipeline.topics.tabela2}")
    private String topicTabela2;

    @Value("${pipeline.topics.tabela3}")
    private String topicTabela3;

    @Bean
    public NewTopic topicTabela1() {
        return TopicBuilder
                .name(topicTabela1)
                .partitions(1)  // 1 partição é suficiente para DEV
                .replicas(1)    // 1 réplica pois temos apenas 1 broker no Docker
                .build();
    }

    @Bean
    public NewTopic topicTabela2() {
        return TopicBuilder
                .name(topicTabela2)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicTabela3() {
        return TopicBuilder
                .name(topicTabela3)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
