package org.projeto.snowflakekafkapipeline.repository;

import org.projeto.snowflakekafkapipeline.model.Orders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OrdersRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY = "SELECT O_ORDERKEY, O_TOTALPRICE, O_ORDERDATE FROM ORDERS LIMIT 1";

    public List<Orders> findAll() {
        log.info("🔍 Executando query na tabela ORDERS...");
        return jdbcTemplate.query(QUERY, rowMapper());
    }

    private RowMapper<Orders> rowMapper() {
        return (rs, rowNum) -> Orders.builder()
                .oOrderkey(rs.getLong("O_ORDERKEY"))
                .oTotalprice(rs.getString("O_TOTALPRICE"))
                .oOrderdate(rs.getString("O_ORDERDATE"))
                .build();
    }
}
