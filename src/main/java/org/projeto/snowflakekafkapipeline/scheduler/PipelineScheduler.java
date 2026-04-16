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

    // Lê os nomes das tabelas do application.properties como um Map
    // Chave = identificador (tabela1, tabela2...), Valor = nome real no Snowflake
    @Value("#{${pipeline.tables.tabela1:'ORDERS'}}")
    private String tabelaOrders;

    @Value("#{${pipeline.tables.tabela2:'CUSTOMER'}}")
    private String tabelaCustomer;

    @Value("#{${pipeline.tables.tabela3:'SUPPLIER'}}")
    private String tabelaSupplier;

    // Lê os nomes dos tópicos do application.properties
    @Value("${pipeline.topics.tabela1}")
    private String topicOrders;

    @Value("${pipeline.topics.tabela2}")
    private String topicCustomer;

    @Value("${pipeline.topics.tabela3}")
    private String topicSupplier;

    // fixedDelayString lê o intervalo do properties em milissegundos
    // fixedDelay = espera o ciclo anterior terminar antes de iniciar o próximo
    // Isso evita execuções sobrepostas caso a extração demore mais que 10 segundos
    @Scheduled(fixedDelayString = "${pipeline.scheduler.interval}")
    public void executePipeline() {
        log.info("⏰ [{}] Iniciando ciclo de extração...", LocalDateTime.now());

        // Monta um mapa relacionando cada tabela ao seu tópico Kafka
        // Assim fica fácil adicionar novas tabelas: só incluir mais uma entrada no mapa
        Map<String, String> tableTopicMap = Map.of(
                tabelaOrders,   topicOrders,
                tabelaCustomer, topicCustomer,
                tabelaSupplier, topicSupplier
        );

        // Itera sobre cada par tabela → tópico e executa a extração
        tableTopicMap.forEach((tableName, topic) -> {
            try {
                extractionService.extractAndSend(tableName, topic);
            } catch (Exception e) {
                // Captura erro por tabela para não interromper as demais extrações
                // Se ORDERS falhar, CUSTOMER e SUPPLIER continuam sendo processadas
                log.error("❌ Erro ao processar tabela {}: {}", tableName, e.getMessage());
            }
        });

        log.info("🏁 Ciclo de extração finalizado.");
    }
}