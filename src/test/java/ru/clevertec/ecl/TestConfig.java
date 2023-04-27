package ru.clevertec.ecl;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "ru.clevertec.ecl.ContextConfig"))
@PropertySource("classpath:test.yml")
@EnableTransactionManagement
public class TestConfig {

    @Value("${jpa.persistence.unit_name}")
    private String persistenceUnitName;

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    @Bean
    public TransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }
}

