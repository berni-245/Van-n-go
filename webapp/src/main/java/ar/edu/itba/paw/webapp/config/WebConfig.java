package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@EnableAsync
@EnableTransactionManagement
@ComponentScan({"ar.edu.itba.paw.webapp.controller",
        "ar.edu.itba.paw.services", "ar.edu.itba.paw.persistence"})
@Configuration
public class WebConfig {
    @Value("classpath:schema.sql")
    private Resource schemaSql;

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setCacheSeconds(-1);
        ms.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        ms.addBasenames("classpath:i18n/messages");

        return ms;
    }

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.postgresql.Driver.class);

        ds.setUrl("jdbc:postgresql://localhost/paw-2024b-01");
        ds.setUsername("paw-2024b-01");
        ds.setPassword("9vegcAS5x");

        return ds;
    }

    @Bean
    public DataSourceInitializer dsInitializer(final DataSource ds) {
        DataSourceInitializer dsi = new DataSourceInitializer();
        dsi.setDataSource(ds);
        dsi.setDatabasePopulator(dsPopulator());
        return dsi;
    }

    private DatabasePopulator dsPopulator() {
        ResourceDatabasePopulator dbp = new ResourceDatabasePopulator();
//        dbp.addScript(schemaSql);
        return dbp;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }



    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setPackagesToScan("ar.edu.itba.paw.models");
        factoryBean.setDataSource(dataSource());

        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);

        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect","org.hibernate.dialect.PostgreSQL92Dialect");
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }


    @Bean(name = "mailProperties")
    public Properties mailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.user", "van.n.go.paw@gmail.com");
        properties.setProperty("mail.password","lrbe ukez jvkk fleu");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("base.prod.url", """
                http://pawserver.it.itba.edu.ar/paw-2024b-01/""");
        return properties;
    }


}
