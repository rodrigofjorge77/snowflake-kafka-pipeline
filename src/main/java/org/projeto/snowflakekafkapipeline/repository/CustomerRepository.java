package org.projeto.snowflakekafkapipeline.repository;

import org.projeto.snowflakekafkapipeline.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY = "SELECT C_CUSTKEY, C_NAME, C_PHONE FROM CUSTOMER LIMIT 1";

    public List<Customer> findAll() {
        log.info("🔍 Executando query na tabela CUSTOMER...");
        return jdbcTemplate.query(QUERY, rowMapper());
    }

    private RowMapper<Customer> rowMapper() {
        return (rs, rowNum) -> Customer.builder()
                .cCustkey(rs.getLong("C_CUSTKEY"))
                .cName(rs.getString("C_NAME"))
                .cPhone(rs.getString("C_PHONE"))
                .build();
    }
}
