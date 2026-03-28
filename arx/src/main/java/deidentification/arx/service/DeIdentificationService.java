package deidentification.arx.service;

import org.springframework.stereotype.Service;
import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DeIdentificationService {

    public void anonymizeExample() throws IOException {
        Data data = Data.create("example/test.csv", StandardCharsets.UTF_8, ';');

        // 1. Define Identifying Attribute (will be removed/hidden)
        data.getDefinition().setAttributeType("name", AttributeType.IDENTIFYING_ATTRIBUTE);

        // 2. Define Zipcode Hierarchy manually
        // Structure: {Original Value, Level 1 Generalization, Level 2, ...}
        String[][] zipcodeHierarchyArray = {
                {"81667", "8166*", "816**", "8****"},
                {"81668", "8166*", "816**", "8****"},
                {"81669", "8166*", "816**", "8****"}
        };

        AttributeType.Hierarchy zipcodeHierarchy = AttributeType.Hierarchy.create(zipcodeHierarchyArray);
        data.getDefinition().setAttributeType("zipcode", zipcodeHierarchy);

        // 3. Set others as INSENSITIVE for now to ensure a solution is found
        data.getDefinition().setAttributeType("age", AttributeType.INSENSITIVE_ATTRIBUTE);
        data.getDefinition().setAttributeType("gender", AttributeType.INSENSITIVE_ATTRIBUTE);

        // 4. Configure Privacy Model (k-anonymity, k=2)
        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new KAnonymity(2));

        // Crucial: Allow ARX to use the hierarchy levels
        config.setSuppressionLimit(0.0d);

        // 5. Run Anonymization
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(data, config);

        if (result.getGlobalOptimum() != null) {
            result.getOutput().save("example/test_anonymized.csv", ';');
            System.out.println("✅ Success! Check example/test_anonymized.csv");
            // You will notice the zipcodes changed to "8166*" to satisfy k=2
        } else {
            System.err.println("❌ No solution found.");
        }
    }
}