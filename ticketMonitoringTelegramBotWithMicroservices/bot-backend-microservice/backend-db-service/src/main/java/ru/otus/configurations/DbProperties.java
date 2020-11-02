package ru.otus.configurations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "hibernate")
@Component
@Getter
@Setter
@NoArgsConstructor
public class DbProperties {

    private Map<String, String> datasource;
    private String jpa;
    private String dialect;
    private Map<String, Object> hbm2ddl;
    private Boolean show_sql;
    private Boolean generate_statistics;
    private Map<String, Object> query;
    private Map<String, Object> connection;
    private Map<String, Object> cache;

}
