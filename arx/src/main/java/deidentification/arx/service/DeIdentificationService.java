package deidentification.arx.service;

import deidentification.arx.config.ArxConfigurationFactory;
import lombok.RequiredArgsConstructor;
import org.deidentifier.arx.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeIdentificationService {

    private final ArxConfigurationFactory configFactory;

    public byte[] anonymize(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        if (bytes.length >= 3 &&
                bytes[0] == (byte)0xEF &&
                bytes[1] == (byte)0xBB &&
                bytes[2] == (byte)0xBF) {
            bytes = Arrays.copyOfRange(bytes, 3, bytes.length);
        }

        File temp = File.createTempFile("arx_input_", ".csv");
        Files.write(temp.toPath(), bytes);
        List<String> lines = Files.readAllLines(temp.toPath(), StandardCharsets.UTF_8);
        String header = lines.getFirst();
        int columnCount = header.split(",").length;
        int rowCount = lines.size() - 1;
        DataSource source = DataSource.createCSVSource(temp, StandardCharsets.UTF_8, ',', true);
        for (int i = 0; i < columnCount; i++) {
            source.addColumn(i);
        }

        Data data = Data.create(source);
        configureAttributes(data);
        ARXConfiguration config = configFactory.build(rowCount);
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(data, config);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.getOutput(false).save(baos, ',');
        temp.delete();
        return baos.toByteArray();
    }

    private void configureAttributes(Data data) {
        DataHandle handle = data.getHandle();
        for (int i = 0; i < handle.getNumColumns(); i++) {
            String attr = handle.getAttributeName(i);
            String lower = attr.toLowerCase();

            if (lower.contains("given") || lower.contains("family") ||
                    lower.contains("id") || lower.contains("isikukood")) {
                data.getDefinition().setAttributeType(attr, AttributeType.IDENTIFYING_ATTRIBUTE);
                continue;
            }

            if (lower.contains("organization") ||
                    lower.contains("author_organization") ||
                    lower.contains("custodian") ||
                    lower.contains("representedorganization")) {
                data.getDefinition().setAttributeType(attr, AttributeType.IDENTIFYING_ATTRIBUTE);
                continue;
            }

            if (lower.contains("streetaddressline") ||
                    lower.contains("street") ||
                    lower.contains("addressline")) {
                data.getDefinition().setAttributeType(attr, AttributeType.IDENTIFYING_ATTRIBUTE);
                continue;
            }

            if (lower.contains("birth") || lower.contains("gender") ||
                    lower.contains("address") || lower.contains("city") ||
                    lower.contains("postal") || lower.contains("county")) {
                data.getDefinition().setAttributeType(attr, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                continue;
            }

            data.getDefinition().setAttributeType(attr, AttributeType.SENSITIVE_ATTRIBUTE);
        }
    }
}