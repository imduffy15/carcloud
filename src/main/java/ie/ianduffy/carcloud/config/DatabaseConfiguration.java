package ie.ianduffy.carcloud.config;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableJpaRepositories("ie.ianduffy.carcloud.repository")
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class DatabaseConfiguration implements EnvironmentAware {

    private Environment environment;

    private RelaxedPropertyResolver propertyResolver;

    @Bean(destroyMethod = "shutdown")
    public DataSource dataSource() {
        log.debug("Configuring Datasource");
        if (propertyResolver.getProperty("url") == null
            && propertyResolver.getProperty("databaseName") == null) {
            log.error("Your database connection pool configuration is incorrect! The application" +
                    "cannot start. Please check your Spring profile, current profiles are: {}",
                Arrays.toString(environment.getActiveProfiles()));

            throw new ApplicationContextException(
                "Database connection pool is not configured correctly");
        }
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(propertyResolver.getProperty("dataSourceClassName"));
        if (propertyResolver.getProperty("url") == null || ""
            .equals(propertyResolver.getProperty("url"))) {
            config.addDataSourceProperty("databaseName",
                propertyResolver.getProperty("databaseName"));
            config.addDataSourceProperty("serverName", propertyResolver.getProperty("serverName"));
        } else {
            config.addDataSourceProperty("url", propertyResolver.getProperty("url"));
        }
        config.addDataSourceProperty("user", propertyResolver.getProperty("username"));
        config.addDataSourceProperty("password", propertyResolver.getProperty("password"));

        if ("com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
            .equals(propertyResolver.getProperty("dataSourceClassName"))) {
            config.addDataSourceProperty("cachePrepStmts",
                propertyResolver.getProperty("cachePrepStmts", "true"));
            config.addDataSourceProperty("prepStmtCacheSize",
                propertyResolver.getProperty("prepStmtCacheSize", "250"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", propertyResolver
                .getProperty("prepStmtCacheSqlLimit", "2048"));
            config.addDataSourceProperty("useServerPrepStmts", propertyResolver
                .getProperty("useServerPrepStmts", "true"));
        }
        return new HikariDataSource(config);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean(name = {"org.springframework.boot.autoconfigure.AutoConfigurationUtils.basePackages"})
    public List<String> getBasePackages() {
        List<String> basePackages = new ArrayList<>();
        basePackages.add("ie.ianduffy.carcloud.domain");
        return basePackages;
    }

    @Bean
    public Hibernate4Module hibernate4Module() {
        return new Hibernate4Module();
    }

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        log.debug("Configuring Liquibase");
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(propertyResolver.getProperty("contexts"));
        return liquibase;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
    }
}

