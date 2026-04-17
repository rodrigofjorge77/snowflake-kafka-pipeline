package org.projeto.snowflakekafkapipeline.repository;

import org.projeto.snowflakekafkapipeline.model.Part;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PartRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY = "SELECT P_PARTKEY, P_NAME, P_BRAND, P_TYPE, P_SIZE FROM PART LIMIT 1";

    public List<Part> findAll() {
        log.info("🔍 Executando query na tabela PART...");
        return jdbcTemplate.query(QUERY, rowMapper());
    }

    private RowMapper<Part> rowMapper() {
        return (rs, rowNum) -> Part.builder()
                .pPartkey(rs.getLong("P_PARTKEY"))
                .pName(rs.getString("P_NAME"))
                .pBrand(rs.getString("P_BRAND"))
                .pType(rs.getString("P_TYPE"))
                .pSize(rs.getInt("P_SIZE"))
                .build();
    }
}