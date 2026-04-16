package org.projeto.snowflakekafkapipeline.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.projeto.snowflakekafkapipeline.repository.CustomerRepository;
import org.projeto.snowflakekafkapipeline.repository.OrdersRepository;
import org.projeto.snowflakekafkapipeline.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnowflakeExtractionService implements ExtractionService {

    private final OrdersRepository ordersRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    // ObjectMapper é o responsável por converter objetos Java em JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void extractAndSend(String tableName, String topic) {
        log.info("▶️  Iniciando extração da tabela {} para o tópico {}", tableName, topic);

        // Busca a lista de objetos correta baseado no nome da tabela
        List<?> records = fetchRecords(tableName);

        if (records.isEmpty()) {
            log.warn("⚠️  Nenhum registro encontrado na tabela {}", tableName);
            return;
        }

        // Envia uma mensagem por linha para o tópico Kafka
        records.forEach(record -> sendToKafka(topic, record));

        log.info("✅ {} registros enviados da tabela {} para o tópico {}", records.size(), tableName, topic);
    }

    // Decide qual repositório chamar baseado no nome da tabela
    // Para adicionar uma nova tabela no futuro: adicione um novo "case" aqui
    private List<?> fetchRecords(String tableName) {
        return switch (tableName.toUpperCase()) {
            case "ORDERS"   -> ordersRepository.findAll();
            case "CUSTOMER" -> customerRepository.findAll();
            case "SUPPLIER" -> supplierRepository.findAll();
            default -> {
                log.error("❌ Tabela não mapeada: {}", tableName);
                yield List.of(); // retorna lista vazia para tabelas desconhecidas
            }
        };
    }

    private void sendToKafka(String topic, Object record) {
        try {
            // Converte o objeto Java em uma String JSON para enviar ao Kafka
            String json = objectMapper.writeValueAsString(record);

            // Envia a mensagem — o callback loga sucesso ou falha de forma assíncrona
            kafkaTemplate.send(topic, json).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("❌ Falha ao enviar mensagem para o tópico {}: {}", topic, ex.getMessage());
                } else {
                    log.debug("📨 Mensagem enviada para o tópico {} | offset: {}",
                            topic,
                            // Offset é a posição da mensagem dentro da partição do Kafka
                            result.getRecordMetadata().offset());
                }
            });

        } catch (JsonProcessingException e) {
            log.error("❌ Erro ao serializar objeto para JSON: {}", e.getMessage());
        }
    }
}