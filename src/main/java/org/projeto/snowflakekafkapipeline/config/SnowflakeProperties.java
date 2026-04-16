package org.projeto.snowflakekafkapipeline.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
// Faz o Spring mapear tudo que começa com "snowflake." no properties para este objeto
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {

    private String url;
    private String username;
    private String password;
    private String database;
    private String schema;
    private String warehouse;
    private String role;
}
