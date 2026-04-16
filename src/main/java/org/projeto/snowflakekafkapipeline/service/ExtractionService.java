package org.projeto.snowflakekafkapipeline.service;

public interface ExtractionService {

    // Contrato: qualquer implementação deve saber extrair e enviar dados para o Kafka
    // O parâmetro tableName permite reusar o mesmo método para qualquer tabela
    void extractAndSend(String tableName, String topic);
}
