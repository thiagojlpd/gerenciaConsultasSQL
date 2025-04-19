package com.example.consultas.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceConfig {

    //spring.datasource.origem
    @Bean(name = "origemDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.origem")
    public DataSource origemDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "origemJdbcTemplate")
    public JdbcTemplate origemJdbcTemplate(@Qualifier("origemDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    //spring.datasource.destino
    @Primary
    @Bean(name = "destinoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.destino")
    public DataSource destinoDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "destinoJdbcTemplate")
    public JdbcTemplate destinoJdbcTemplate(@Qualifier("destinoDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
