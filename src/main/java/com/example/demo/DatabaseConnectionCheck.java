package com.example.demo;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConnectionCheck {
	private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionCheck.class);

	@Bean
	ApplicationRunner verifySqlServerConnection(DataSource dataSource) {
		return args -> {
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			log.info("SQL Server connection OK (SELECT 1 -> {})", one);
		};
	}
}

