package ir.carpino.tracker.configuraiton;

import ir.carpino.tracker.entity.mysql.DriverLocation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "trackerMysqlEntityManagerFactory",
        transactionManagerRef = "trackerMysqlTransactionManager",
        basePackages = "ir.carpino.tracker.repository.tracker"
)
public class TrackerDatabaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.mysql.datasource.tracker")
    public DataSource trackerMysqlDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "trackerMysqlEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean trackerMysqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(trackerMysqlDataSource())
                .packages(DriverLocation.class)
                .persistenceUnit("mysqlTracker")
                .build();
    }

    @Bean(name = "trackerMysqlTransactionManager")
    public PlatformTransactionManager trackerMysqlTransactionManager(@Qualifier("trackerMysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
