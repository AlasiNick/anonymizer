package web.api.infrastructure.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.presidio")
public class PresidioProperties {

    private String baseUrl;
}
