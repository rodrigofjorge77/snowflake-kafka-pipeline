package org.projeto.snowflakekafkapipeline.repository;

import org.projeto.snowflakekafkapipeline.model.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SupplierRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY = "SELECT S_SUPPKEY, S_NAME, S_ACCTBAL FROM SUPPLIER LIMIT 1";

    public List<Supplier> findAll() {
        log.info("🔍 Executando query na tabela SUPPLIER...");
        return jdbcTemplate.query(QUERY, rowMapper());
    }

    private RowMapper<Supplier> rowMapper() {
        return (rs, rowNum) -> Supplier.builder()
                .sSuppkey(rs.getLong("S_SUPPKEY"))
                .sName(rs.getString("S_NAME"))
                .sAcctbal(rs.getString("S_ACCTBAL"))
                .build();
    }
}
