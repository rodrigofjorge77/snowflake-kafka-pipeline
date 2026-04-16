# snowflake-kafka-pipeline

## Arquitetura

```
┌─────────────────────────────────────────────────────────────────┐
│                          SNOWFLAKE                              │
│                                                                 │
│   ┌─────────────┐   ┌─────────────────┐   ┌────────────────┐   │
│   │   ORDERS    │   │    CUSTOMER     │   │    SUPPLIER    │   │
│   └──────┬──────┘   └────────┬────────┘   └───────┬────────┘   │
└──────────┼───────────────────┼────────────────────┼────────────┘
           │                   │                    │
           └───────────────────┼────────────────────┘
                               │ JDBC (leitura a cada 10s)
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT (Java 17)                       │
│                                                                 │
│  ┌──────────────┐    ┌──────────────────────────────────────┐   │
│  │  Scheduler   │───▶│       SnowflakeExtractionService     │   │
│  │ (a cada 10s) │    │                                      │   │
│  └──────────────┘    │  ┌────────────┐  ┌────────────────┐  │   │
│                      │  │ Repository │  │  ObjectMapper  │  │   │
│                      │  │  (JDBC)    │─▶│  (Java → JSON) │  │   │
│                      │  └────────────┘  └───────┬────────┘  │   │
│                      └──────────────────────────┼───────────┘   │
│                                                 │               │
│                                    KafkaTemplate│               │
└─────────────────────────────────────────────────┼───────────────┘
                                                  │ envia 1 msg por linha
                                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                      KAFKA (Docker / WSL2)                      │
│                                                                 │
│   ┌──────────────┐  ┌─────────────────┐  ┌──────────────────┐  │
│   │ topic-orders │  │ topic-customer  │  │ topic-supplier   │  │
│   └──────────────┘  └─────────────────┘  └──────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Fluxo

A cada **10 segundos** o scheduler dispara e executa o seguinte ciclo para cada tabela configurada:

1. **Leitura** — o repositório executa um `SELECT` na tabela do Snowflake via JDBC
2. **Serialização** — cada linha retornada é convertida em uma mensagem JSON pelo Jackson
3. **Envio** — cada mensagem JSON é enviada individualmente para o tópico Kafka correspondente

Cada tabela possui seu próprio tópico Kafka. O pipeline é extensível — adicionar uma nova tabela requer apenas criar um novo model, repositório e uma linha de configuração no `application.properties`.

---

## Tecnologias

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 17 | Linguagem |
| Spring Boot | 4.0.x | Framework principal |
| Spring Kafka | - | Produtor de mensagens Kafka |
| Spring JDBC | - | Acesso ao banco via JdbcTemplate |
| Snowflake JDBC | 3.15.0 | Driver de conexão com o Snowflake |
| Jackson | - | Serialização JSON |
| Lombok | - | Redução de boilerplate |
| Apache Kafka | 7.5.0 | Broker de mensagens |
| Docker | - | Container do Kafka (via WSL2) |

---

## Estrutura do Projeto

```
src/main/java/com/seunome/pipeline/
│
├── config/
│   ├── SnowflakeConfig.java           # DataSource e JdbcTemplate do Snowflake
│   ├── SnowflakeProperties.java       # Mapeamento das propriedades do Snowflake
│   ├── KafkaProducerConfig.java       # Configuração do Kafka Producer
│   └── KafkaTopicConfig.java          # Criação automática dos tópicos Kafka
│
├── model/
│   ├── Orders.java                    # Representa a tabela ORDERS
│   ├── Customer.java                  # Representa a tabela CUSTOMER
│   └── Supplier.java                  # Representa a tabela SUPPLIER
│
├── repository/
│   ├── OrdersRepository.java          # Query e RowMapper para ORDERS
│   ├── CustomerRepository.java        # Query e RowMapper para CUSTOMER
│   └── SupplierRepository.java        # Query e RowMapper para SUPPLIER
│
├── service/
│   ├── ExtractionService.java         # Interface do serviço de extração
│   └── SnowflakeExtractionService.java # Implementação — extrai e envia ao Kafka
│
└── scheduler/
    └── PipelineScheduler.java         # Agendamento a cada 10 segundos
```

---

## Pré-requisitos

- Java 17
- Maven 3.8+
- Docker com WSL2 (Ubuntu) para rodar o Kafka
- Conta no Snowflake com credenciais de acesso

---

## Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/SEU_USUARIO/snowflake-kafka-pipeline.git
cd snowflake-kafka-pipeline
```

### 2. Configure as credenciais

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp src/main/resources/application-example.properties src/main/resources/application-dev.properties
```

Edite o `application-dev.properties`:

```properties
# Snowflake
snowflake.url=jdbc:snowflake://<account>.snowflakecomputing.com
snowflake.username=SEU_USUARIO
snowflake.password=SUA_SENHA
snowflake.database=SEU_BANCO
snowflake.schema=SEU_SCHEMA
snowflake.warehouse=SEU_WAREHOUSE
snowflake.role=SEU_ROLE

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Scheduler (milissegundos)
pipeline.scheduler.interval=10000

# Tópicos Kafka
pipeline.topics.tabela1=topic-orders
pipeline.topics.tabela2=topic-customer
pipeline.topics.tabela3=topic-supplier

# Tabelas Snowflake
pipeline.tables.tabela1=ORDERS
pipeline.tables.tabela2=CUSTOMER
pipeline.tables.tabela3=SUPPLIER
```

> ⚠️ Os arquivos `application-dev.properties` e `application-prod.properties` estão no `.gitignore` e **nunca devem ser commitados**.

### 3. Suba o Kafka via Docker (WSL2)

```bash
docker-compose up -d
```

### 4. Execute a aplicação

Via IntelliJ: clique em **Run** (▶️)

Via terminal:
```bash
mvn spring-boot:run
```

---

## Exemplo de mensagens no Kafka

**topic-orders:**
```json
{"oOrderkey": 1, "oTotalprice": "173665.47", "oOrderdate": "1996-01-02"}
```

**topic-customer:**
```json
{"cCustkey": 1, "cName": "Customer#000000001", "cPhone": "25-989-741-2988"}
```

**topic-supplier:**
```json
{"sSuppkey": 1, "sName": "Supplier#000000001", "sAcctbal": "5755.94"}
```

---

## Como adicionar uma nova tabela

1. Criar `NovaTabela.java` em `model/` com os atributos da tabela
2. Criar `NovaTabelaRepository.java` em `repository/` com a query e o `RowMapper`
3. Adicionar o `case` no `switch` do `SnowflakeExtractionService.java`
4. Adicionar as variáveis no `PipelineScheduler.java`
5. Adicionar no `application-dev.properties`:

```properties
pipeline.topics.tabela4=topic-nova-tabela
pipeline.tables.tabela4=NOVA_TABELA
```

Nenhuma outra classe precisa ser alterada.

---

## Variáveis de ambiente por perfil

| Arquivo | Ambiente | Commitado? |
|---|---|---|
| `application.properties` | Base (todos) | ✅ Sim |
| `application-example.properties` | Modelo para devs | ✅ Sim |
| `application-dev.properties` | Desenvolvimento | ❌ Não |
| `application-prod.properties` | Produção | ❌ Não |

---

## Licença

MIT
