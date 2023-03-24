package ru.clevertec.ecl;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.clevertec.ecl.data.connection.ConfigurationManager;

@Configuration
@ComponentScan
public class ContextConfig {

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        ConfigurationManager configurationManager = new ConfigurationManager();
        dataSource.setJdbcUrl(configurationManager.getProperty("db.url"));
        dataSource.setPassword(configurationManager.getProperty("db.password"));
        dataSource.setUsername(configurationManager.getProperty("db.user"));
        dataSource.setMaximumPoolSize(Integer.parseInt(configurationManager.getProperty("db.pool_size")));
        dataSource.setDriverClassName(configurationManager.getProperty("db.driver"));
        return dataSource;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

}
