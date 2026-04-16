package org.projeto.snowflakekafkapipeline.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    // Lê o endereço do Kafka direto do application.properties
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // Endereço do broker Kafka (no seu caso: localhost:9092 via WSL2)
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Chave da mensagem será uma String (ex: nome da tabela)
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Valor da mensagem será uma String (o JSON da linha da tabela)
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Garante que a mensagem só é confirmada após ser gravada por todos os brokers
        // Em produção com múltiplos brokers isso evita perda de dados
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        // Número de tentativas automáticas em caso de falha de envio
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // KafkaTemplate é o objeto que usaremos para enviar mensagens nos serviços
        return new KafkaTemplate<>(producerFactory());
    }
}
