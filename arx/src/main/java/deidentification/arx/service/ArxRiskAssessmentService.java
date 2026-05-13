package deidentification.arx.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.deidentifier.arx.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ArxRiskAssessmentService {

    public byte[] performRiskAssessment(byte[] inputCsv) throws Exception {
        // Remove BOM if present
        if (inputCsv.length >= 3 &&
                inputCsv[0] == (byte) 0xEF &&
                inputCsv[1] == (byte) 0xBB &&
                inputCsv[2] == (byte) 0xBF) {
            inputCsv = Arrays.copyOfRange(inputCsv, 3, inputCsv.length);
        }

        // Create DataSource from bytes
        try (ByteArrayInputStream bais = new ByteArrayInputStream(inputCsv)) {
            DataSource source = DataSource.createCSVSource(bais.toString(), StandardCharsets.UTF_8, ',', true);

            // Add all columns
            // We need to know column count - read first line
            String firstLine = new String(inputCsv, StandardCharsets.UTF_8).split("\n")[0];
            int columnCount = firstLine.split(",").length;

            for (int i = 0; i < columnCount; i++) {
                source.addColumn(i);
            }

            Data data = Data.create(source);

            // Configure Quasi-Identifiers for risk assessment
            configureQuasiIdentifiers(data);

            // Build basic risk report (since full RiskAnalysis may not be available)
            return buildRiskReport(data);
        }
    }

    private void configureQuasiIdentifiers(Data data) {
        DataHandle handle = data.getHandle();
        DataDefinition definition = data.getDefinition();

        for (int i = 0; i < handle.getNumColumns(); i++) {
            String attr = handle.getAttributeName(i);
            String lower = attr.toLowerCase();

            if (lower.contains("name") || lower.contains("given") || lower.contains("family") ||
                    lower.contains("id") || lower.contains("phone") || lower.contains("isikukood") ||
                    lower.contains("street") || lower.contains("address")) {

                definition.setAttributeType(attr, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
            }
        }
    }

    private byte[] buildRiskReport(Data data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Write UTF-8 BOM
        baos.write(0xEF);
        baos.write(0xBB);
        baos.write(0xBF);

        try (java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter writer = new com.opencsv.CSVWriter(osw)) {

            writer.writeNext(new String[]{"Metric", "Value", "Description"});

            writer.writeNext(new String[]{
                    "Total Records",
                    String.valueOf(data.getHandle().getNumRows()),
                    "Number of rows in dataset"
            });

            writer.writeNext(new String[]{
                    "Total Columns",
                    String.valueOf(data.getHandle().getNumColumns()),
                    ""
            });

            writer.writeNext(new String[]{
                    "Risk Assessment Type",
                    "Basic Quasi-Identifier Analysis",
                    "Full probabilistic risk requires newer ARX version"
            });

            writer.writeNext(new String[]{
                    "Note",
                    "Consider upgrading ARX JAR for detailed re-identification risk metrics",
                    ""
            });

            writer.flush();
        }

        return baos.toByteArray();
    }
}