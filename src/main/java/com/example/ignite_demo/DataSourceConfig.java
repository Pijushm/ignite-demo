package com.example.ignite_demo;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

  @Bean
  public static DataSource dataSource() {
    return DataSourceBuilder.create()
        .driverClassName("org.postgresql.Driver") // PostgreSQL driver
        .username("test")                     // your DB username
        .password("test")                // your DB password
        .url("jdbc:postgresql://localhost:5432/ignite_db") // PostgreSQL URL
        .build();
  }
}
