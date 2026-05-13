package deidentification.arx.config;

import deidentification.arx.config.properties.ArxProperties;
import lombok.RequiredArgsConstructor;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.criteria.KAnonymity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArxConfigurationFactory {

    private final ArxProperties props;

    public ARXConfiguration build(int rowCount) {
        int k = Math.min(props.getK(), rowCount);
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(k));
        config.setSuppressionLimit(props.getSuppressionLimit());
        return config;
    }
}
