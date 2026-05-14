package deidentification.arx.config;

import deidentification.arx.config.properties.ArxProperties;
import lombok.RequiredArgsConstructor;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.KAnonymity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArxConfigurationFactory {

    private final ArxProperties props;

    public ARXConfiguration build(int rowCount, List<String> sensitiveAttributes) {
        int k = Math.min(props.getK(), rowCount);
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(k));
        config.setSuppressionLimit(props.getSuppressionLimit());

        for (String attr : sensitiveAttributes) {
            int l = Math.min(props.getL(), rowCount);
            config.addPrivacyModel(new DistinctLDiversity(attr, l));
        }

        return config;
    }
}
