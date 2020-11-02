package ru.otus.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableRetry
@EnableJpaRepositories(basePackages = {"ru.otus.db.repository"})
@EntityScan("ru.otus.backend.model")
@RequiredArgsConstructor
@ComponentScan("ru.otus")
public class DBConfigs {
    private final DbProperties dbProperties;

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbProperties.getDatasource().get("driverClassName"));
        dataSource.setUrl(dbProperties.getDatasource().get("url"));
        dataSource.setUsername(dbProperties.getDatasource().get("username"));
        dataSource.setPassword(dbProperties.getDatasource().get("password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setPackagesToScan("ru.otus.backend.model");
        em.setDataSource(getDataSource());
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", dbProperties.getDialect());
        hibernateProperties.put("hibernate.hbm2ddl.auto", dbProperties.getHbm2ddl().get("auto"));
        hibernateProperties.put("hibernate.show_sql", dbProperties.getShow_sql());
        hibernateProperties.put("hibernate.generate_statistics", dbProperties.getGenerate_statistics());
        hibernateProperties.put("hibernate.connection.enable_lazy_load_no_trans", dbProperties.getConnection().get("enable_lazy_load_no_trans"));
        hibernateProperties.put("hibernate.query.substitutions", dbProperties.getQuery().get("substitutions"));
        hibernateProperties.put("hibernate.cache.use_query_cache", dbProperties.getCache().get("use_query_cache"));
        hibernateProperties.put("hibernate.cache.use_second_level_cache", dbProperties.getCache().get("use_second_level_cache"));
        hibernateProperties.put("hibernate.cache.region.factory_class", env.getProperty("hibernate.cache.region.factory_class"));
        hibernateProperties.put("net.sf.ehcache.configurationResourceName", env.getProperty("net.sf.ehcache.configurationResourceName"));
        return hibernateProperties;
    }

}


