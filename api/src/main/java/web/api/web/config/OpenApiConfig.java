package web.api.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI anonymizerApi() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("HL7 CDA Anonymizer API")
                                .version("1.0")
                                .description("""
                                        Stateless GDPR-compliant HL7 CDA
                                        transformation and anonymization API
                                        """)
                );
    }
}