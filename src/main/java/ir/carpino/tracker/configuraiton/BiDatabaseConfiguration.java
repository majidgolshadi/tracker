package ir.carpino.tracker.configuraiton;

import ir.carpino.tracker.entity.mysql.bi.BiDriverLocation;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "biMysqlEntityManager",
        transactionManagerRef = "biMysqlTransactionManager",
        basePackages = "ir.carpino.tracker.entity.mysql.bi"
)
public class BiDatabaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.mysql.datasource.bi")
    public DataSource biMysqlDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Primary
    @Bean(name = "biMysqlEntityManager")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(biMysqlDataSource())
                .packages(BiDriverLocation.class)
                .persistenceUnit("mysqlPU")
                .build();
    }

    @Primary
    @Bean(name = "biMysqlTransactionManager")
    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("biMysqlEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}