package com.example.consultas.config.painel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfigPainel {

    //spring.datasource.origem.sigapce
    @Bean(name = "origemSIGAPCEDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.origem.sigapce")
    public DataSource origemDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "origemSIGAPCEJdbcTemplate")
    public JdbcTemplate origemJdbcTemplate(@Qualifier("origemSIGAPCEDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

}
