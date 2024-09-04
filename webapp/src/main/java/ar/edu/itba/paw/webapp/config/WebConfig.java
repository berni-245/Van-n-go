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
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

@EnableWebMvc
@ComponentScan({"ar.edu.itba.paw.webapp.controller",
        "ar.edu.itba.paw.services", "ar.edu.itba.paw.persistence"})
@Configuration
public class WebConfig {
    @Value("classpath:schema.sql")
    private Resource schemaSql;

//    Doesn't work. See https://github.com/spring-projects/spring-framework/issues/19999
//    @Value("classpath:triggers.sql")
//    private Resource triggersSql;

    @Bean
    public ViewResolver viewResolver() {
        final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        return viewResolver;
    }

    @Bean
    public MessageSource messageSource() {
        final ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setCacheSeconds(5); // Not performant, only for development.
        ms.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        ms.addBasenames("classpath:i18n/messages");

        return ms;
    }

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource ds = new SimpleDriverDataSource();

        ds.setDriverClass(org.postgresql.Driver.class);

        // lo de abajo cambiará en el servidor que usaremos nosotros
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
        dbp.addScript(schemaSql);
//        dbp.addScript(triggersSql);
        return dbp;
    }
}
