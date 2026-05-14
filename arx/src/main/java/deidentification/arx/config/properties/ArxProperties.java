package deidentification.arx.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.arx")
public class ArxProperties {

    private int k;
    private int l;
    private double suppressionLimit;
}