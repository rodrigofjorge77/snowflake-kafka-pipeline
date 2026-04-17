package org.projeto.snowflakekafkapipeline.scheduler;

import org.projeto.snowflakekafkapipeline.service.ExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineScheduler {

    private final ExtractionService extractionService;

    // Leitura simples das propriedades com ${...} sem SpEL
    @Value("${pipeline.tables.tabela1}")
    private String tabelaOrders;

    @Value("${pipeline.tables.tabela2}")
    private String tabelaCustomer;

    @Value("${pipeline.tables.tabela3}")
    private String tabelaSupplier;

    @Value("${pipeline.tables.tabela4}")
    private String tabelaPart;

    @Value("${pipeline.topics.tabela1}")
    private String topicOrders;

    @Value("${pipeline.topics.tabela2}")
    private String topicCustomer;

    @Value("${pipeline.topics.tabela3}")
    private String topicSupplier;

    @Value("${pipeline.topics.tabela4}")
    private String topicPart;

    @Scheduled(fixedDelayString = "${pipeline.scheduler.interval}")
    public void executePipeline() {
        log.info("⏰ [{}] Iniciando ciclo de extração...", LocalDateTime.now());

        // Mapa relacionando cada tabela ao seu tópico Kafka
        Map<String, String> tableTopicMap = Map.of(
                tabelaOrders,   topicOrders,
                tabelaCustomer, topicCustomer,
                tabelaSupplier, topicSupplier,
                tabelaPart,     topicPart
        );

        // Itera sobre cada par tabela → tópico
        // Captura erro por tabela para não interromper as demais em caso de falha
        tableTopicMap.forEach((tableName, topic) -> {
            try {
                extractionService.extractAndSend(tableName, topic);
            } catch (Exception e) {
                log.error("❌ Erro ao processar tabela {}: {}", tableName, e.getMessage());
            }
        });

        log.info("🏁 Ciclo de extração finalizado.");
    }
}