package org.projeto.snowflakekafkapipeline.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SnowflakeConfig {

    private final SnowflakeProperties props;

    @Bean
    public DataSource snowflakeDataSource() {
        try {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();

            // Driver JDBC oficial do Snowflake
            dataSource.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
            dataSource.setUrl(props.getUrl());
            dataSource.setUsername(props.getUsername());
            dataSource.setPassword(props.getPassword());

            // Propriedades extras do Snowflake passadas via objeto Properties
            // O Snowflake exige database, schema, warehouse e role como parâmetros separados
            Properties extraProps = new Properties();
            extraProps.setProperty("db", props.getDatabase());
            extraProps.setProperty("schema", props.getSchema());
            extraProps.setProperty("warehouse", props.getWarehouse());
            extraProps.setProperty("role", props.getRole());
            dataSource.setConnectionProperties(extraProps);

            log.info("✅ Conexão com Snowflake configurada com sucesso.");
            return dataSource;

        } catch (Exception e) {
            throw new RuntimeException("❌ Falha ao configurar o DataSource do Snowflake", e);
        }
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource snowflakeDataSource) {
        return new JdbcTemplate(snowflakeDataSource);
    }
}