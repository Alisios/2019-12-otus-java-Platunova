package ru.otus.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableCaching(mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackages = {"ru.otus.repository"})
@EntityScan("ru.otus.backend.model")
public class DBConfiguration {

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("hibernate.datasource.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("hibernate.datasource.url"));
        dataSource.setUsername(env.getRequiredProperty("hibernate.datasource.username"));
        dataSource.setPassword(env.getRequiredProperty("hibernate.datasource.password"));
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
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
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
        hibernateProperties.put("hibernate.dialect", env.getProperty("hibernate.jpa.properties.dialect"));
        hibernateProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.jpa.hbm2ddl.auto"));
        hibernateProperties.put("hibernate.show_sql", env.getProperty("hibernate.jpa.show_sql"));

        hibernateProperties.put("hibernate.generate_statistics", env.getProperty("hibernate.jpa.generate_statistics"));
        hibernateProperties.put("hibernate.connection.enable_lazy_load_no_trans", env.getProperty("hibernate.jpa.connection.enable_lazy_load_no_trans"));
        hibernateProperties.put("hibernate.query.substitutions", env.getProperty("hibernate.jpa.query.substitutions"));
        hibernateProperties.put("hibernate.cache.use_query_cache", env.getProperty("hibernate.cache.use_query_cache"));
        hibernateProperties.put("hibernate.cache.region.factory_class", env.getProperty("hibernate.cache.region.factory_class"));
        hibernateProperties.put("hibernate.cache.use_second_level_cache", env.getProperty("hibernate.cache.use_second_level_cache"));
        hibernateProperties.put("net.sf.ehcache.configurationResourceName", env.getProperty("net.sf.ehcache.configurationResourceName"));
        //hibernateProperties.put("hibernate.javax.cache.provider", env.getProperty("hibernate.javax.cache.provider"));
        //hibernateProperties.put("net.sf.ehcache.configurationResourceName", env.getProperty("net.sf.ehcache.configurationResourceName"));
        return hibernateProperties;
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheManagerFactory() {
        EhCacheManagerFactoryBean cacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        cacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        cacheManagerFactoryBean.setShared(true);
        return cacheManagerFactoryBean;
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehCacheManagerFactory().getObject());
        cacheManager.setTransactionAware(true);
        return cacheManager;
    }

}


