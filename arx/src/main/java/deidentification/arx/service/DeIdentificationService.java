package deidentification.arx.service;

import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class DeIdentificationService {

    public byte[] anonymize(MultipartFile file) throws Exception {

        DataSource source = DataSource.createCSVSource(
                file.getInputStream().toString(),
                        StandardCharsets.UTF_8,
                        ',',
                        true);

        Data data = Data.create(source);
        configureAttributes(data);
        ARXConfiguration config = buildConfiguration();
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(data, config);
        if (result.getGlobalOptimum() == null) {
            throw new RuntimeException(
                    "No anonymization solution found"
            );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.getOutput(false).save(baos, ',');

        return baos.toByteArray();
    }

    private void configureAttributes(Data data) {
        DataHandle handle = data.getHandle();

        for (int i = 0; i < handle.getNumColumns(); i++) {

            String attribute = handle.getAttributeName(i);
            String lower = attribute.toLowerCase();

            if (lower.contains("given")
                    || lower.contains("family")
                    || lower.contains("id")) {

                data.getDefinition().setAttributeType(
                        attribute,
                        AttributeType.IDENTIFYING_ATTRIBUTE);

                continue;
            }

            if (lower.contains("birth")
                    || lower.contains("gender")
                    || lower.contains("address")
                    || lower.contains("city")
                    || lower.contains("postal")
                    || lower.contains("country")) {

                data.getDefinition().setAttributeType(
                        attribute,
                        AttributeType.QUASI_IDENTIFYING_ATTRIBUTE
                );

                continue;
            }

            data.getDefinition().setAttributeType(
                    attribute,
                    AttributeType.SENSITIVE_ATTRIBUTE
            );
        }
    }

    private ARXConfiguration buildConfiguration() {

        ARXConfiguration config =
                ARXConfiguration.create();

        config.addPrivacyModel(
                new KAnonymity(2)
        );

        config.setSuppressionLimit(0.05d);

        return config;
    }
}