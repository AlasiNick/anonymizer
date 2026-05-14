package deidentification.arx.service;

import deidentification.arx.config.ArxConfigurationFactory;
import deidentification.arx.domain.type.FieldType;
import deidentification.arx.utility.FieldClassifier;
import lombok.RequiredArgsConstructor;
import org.deidentifier.arx.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

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
        int columnCount = header.split(",", -1).length;
        int rowCount = lines.size() - 1;
        DataSource source = DataSource.createCSVSource(temp, StandardCharsets.UTF_8, ',', true);
        for (int i = 0; i < columnCount; i++) {
            source.addColumn(i);
        }

        Data data = Data.create(source);
        List<String> sensitiveAttributes = configureAttributes(data);
        ARXConfiguration config = configFactory.build(rowCount, sensitiveAttributes);
        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result = anonymizer.anonymize(data, config);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        result.getOutput(false).save(baos, ',');
        temp.delete();
        return baos.toByteArray();
    }

    private List<String> configureAttributes(Data data) {
        DataHandle handle = data.getHandle();
        List<String> sensitiveAttributes = new ArrayList<>();
        for (int col = 0; col < handle.getNumColumns(); col++) {
            String attr = handle.getAttributeName(col).trim();
            FieldType type = FieldClassifier.classify(attr);

            AttributeType arxType = switch (type) {
                case DIRECT_IDENTIFIER -> AttributeType.IDENTIFYING_ATTRIBUTE;
                case QUASI_IDENTIFIER  -> AttributeType.QUASI_IDENTIFYING_ATTRIBUTE;
                case SENSITIVE         -> AttributeType.SENSITIVE_ATTRIBUTE;
                default                -> AttributeType.INSENSITIVE_ATTRIBUTE;
            };

            data.getDefinition().setAttributeType(attr, arxType);

            if (type == FieldType.SENSITIVE) {
                sensitiveAttributes.add(attr);
            }

            if (type == FieldType.QUASI_IDENTIFIER) {

                if (attr.toLowerCase().contains("birth")) {
                    data.getDefinition().setMinimumGeneralization(attr, 1);
                }

                AttributeType.Hierarchy hierarchy = buildHierarchy(attr, handle, col);
                if (hierarchy != null) {
                    data.getDefinition().setHierarchy(attr, hierarchy);
                }
            }
        }

        return sensitiveAttributes;
    }

    private AttributeType.Hierarchy buildHierarchy(String attr, DataHandle handle, int colIndex) {
        String lower = attr.toLowerCase();

        if (lower.contains("birth")) {
            return buildDateHierarchy(handle, colIndex);
        }

        return buildSuppressHierarchy(handle, colIndex);
    }

    private AttributeType.Hierarchy buildDateHierarchy(DataHandle handle, int colIndex) {
        Set<String> uniqueValues = collectUniqueValues(handle, colIndex);
        String[][] matrix = uniqueValues.stream()
                .map(val -> new String[]{
                        val,
                        val.length() >= 4 ? val.substring(0, 4) : "*",
                        "*"
                })
                .toArray(String[][]::new);

        return AttributeType.Hierarchy.create(matrix);
    }

    private AttributeType.Hierarchy buildSuppressHierarchy(DataHandle handle, int colIndex) {
        Set<String> uniqueValues = collectUniqueValues(handle, colIndex);
        String[][] matrix = uniqueValues.stream()
                .map(val -> new String[]{val, "*"})
                .toArray(String[][]::new);

        return AttributeType.Hierarchy.create(matrix);
    }

    private Set<String> collectUniqueValues(DataHandle handle, int colIndex) {
        Set<String> uniqueValues = new LinkedHashSet<>();

        for (int row = 0; row < handle.getNumRows(); row++) {
            String val = handle.getValue(row, colIndex);

            if (val != null) {
                val = val.trim().replace("\"", "");
            }

            if (val != null && !val.isBlank()) {
                uniqueValues.add(val);
            }
        }

        return uniqueValues;
    }
}
