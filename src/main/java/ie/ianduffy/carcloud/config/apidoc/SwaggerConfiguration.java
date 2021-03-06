package ie.ianduffy.carcloud.config.apidoc;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import ie.ianduffy.carcloud.config.Constants;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

@Configuration
@EnableSwagger
@Profile("!" + Constants.SPRING_PROFILE_TEST)
public class SwaggerConfiguration implements EnvironmentAware {

    private static final String DEFAULT_INCLUDE_PATTERN = "/app/.*";

    private RelaxedPropertyResolver propertyResolver;

    private ApiInfo apiInfo() {
        return new ApiInfo(
            propertyResolver.getProperty("title"),
            propertyResolver.getProperty("description"),
            propertyResolver.getProperty("termsOfServiceUrl"),
            propertyResolver.getProperty("contact"),
            propertyResolver.getProperty("license"),
            propertyResolver.getProperty("licenseUrl"));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "swagger.");
    }

    @Bean
    public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin(SpringSwaggerConfig springSwaggerConfig) {
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
            .apiInfo(apiInfo())
            .genericModelSubstitutes(ResponseEntity.class)
            .includePatterns(DEFAULT_INCLUDE_PATTERN);
    }
}
