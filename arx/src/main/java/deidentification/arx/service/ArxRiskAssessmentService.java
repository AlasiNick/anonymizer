package deidentification.arx.service;

import com.opencsv.CSVWriter;
import org.deidentifier.arx.*;
import org.deidentifier.arx.risk.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

@Service
public class ArxRiskAssessmentService {

    public byte[] performRiskAssessment(byte[] inputCsv) throws Exception {
        if (inputCsv.length >= 3 &&
                inputCsv[0] == (byte) 0xEF &&
                inputCsv[1] == (byte) 0xBB &&
                inputCsv[2] == (byte) 0xBF) {
            inputCsv = Arrays.copyOfRange(inputCsv, 3, inputCsv.length);
        }

        File tempFile = File.createTempFile("arx_risk_input_", ".csv");
        try {
            Files.write(tempFile.toPath(), inputCsv);

            DataSource source = DataSource.createCSVSource(tempFile, StandardCharsets.UTF_8, ',', true);
            String firstLine = new String(inputCsv, StandardCharsets.UTF_8).split("\n")[0];
            int columnCount = firstLine.split(",").length;
            for (int i = 0; i < columnCount; i++) {
                source.addColumn(i);
            }

            Data data = Data.create(source);
            configureQuasiIdentifiers(data);
            return buildRiskReport(data);

        } finally {
            tempFile.delete();
        }
    }

    private void configureQuasiIdentifiers(Data data) {
        DataHandle handle = data.getHandle();
        DataDefinition definition = data.getDefinition();
        for (int i = 0; i < handle.getNumColumns(); i++) {
            String attr = handle.getAttributeName(i).toLowerCase();
            if (attr.contains("name") || attr.contains("given") || attr.contains("family") ||
                    attr.contains("id") || attr.contains("phone") || attr.contains("street") ||
                    attr.contains("address") || attr.contains("location")) {
                definition.setAttributeType(handle.getAttributeName(i), AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
            }
        }
    }

    private byte[] buildRiskReport(Data data) throws Exception {
        DataHandle handle = data.getHandle();
        ARXPopulationModel population = ARXPopulationModel.create(handle.getNumRows(), 0.01d);
        RiskEstimateBuilder builder = handle.getRiskEstimator(population);
        RiskModelSampleRisks prosecutorRisk = builder.getSampleBasedReidentificationRisk();
        RiskModelSampleUniqueness journalistRisk = builder.getSampleBasedUniquenessRisk();
        prosecutorRisk.getAverageRisk();
        RiskModelSampleSummary summary = builder.getSampleBasedRiskSummary(0.2d); // 20% threshold

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(0xEF); baos.write(0xBB); baos.write(0xBF);

        try (OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter writer = new CSVWriter(osw)) {

            writer.writeNext(new String[]{"Metric", "Value", "Description"});

            writer.writeNext(new String[]{"", "", ""});
            writer.writeNext(new String[]{"=== Dataset Summary ===", "", ""});
            writer.writeNext(new String[]{"Total Records",    String.valueOf(handle.getNumRows()), "Number of rows in dataset"});
            writer.writeNext(new String[]{"Total Columns",    String.valueOf(handle.getNumColumns()), "Number of attributes"});

            writer.writeNext(new String[]{"", "", ""});
            writer.writeNext(new String[]{"=== Prosecutor Model ===", "", "Attacker knows the target is in the dataset"});
            writer.writeNext(new String[]{"Highest Risk",     pct(prosecutorRisk.getHighestRisk()), "Maximum re-identification risk for any single record"});
            writer.writeNext(new String[]{"Average Risk",     pct(prosecutorRisk.getAverageRisk()), "Mean re-identification risk across all records"});
            writer.writeNext(new String[]{"Lowest Risk",      pct(prosecutorRisk.getLowestRisk()), "Minimum re-identification risk for any single record"});
            writer.writeNext(new String[]{"Records at Highest Risk", pct(prosecutorRisk.getFractionOfRecordsAffectedByHighestRisk()), "Fraction of records sharing the highest risk"});
            writer.writeNext(new String[]{"Records at Lowest Risk",  pct(prosecutorRisk.getFractionOfRecordsAffectedByLowestRisk()), "Fraction of records sharing the lowest risk"});

            writer.writeNext(new String[]{"", "", ""});
            writer.writeNext(new String[]{"=== Journalist Model ===", "", "Attacker has no prior knowledge of who is in the dataset"});
            writer.writeNext(new String[]{"Sample Unique Records", pct(journalistRisk.getFractionOfUniqueRecords()), "Fraction of records that are unique in the sample — highest journalist risk"});

            writer.writeNext(new String[]{"", "", ""});
            writer.writeNext(new String[]{"=== Marketer Model ===", "", "Attacker succeeds if ANY record in a group is re-identified"});
            writer.writeNext(new String[]{"Success Rate (Marketer)", pct(summary.getMarketerRisk().getSuccessRate()), "Expected fraction of records successfully re-identified"});

            writer.writeNext(new String[]{"", "", ""});
            writer.writeNext(new String[]{"=== Risk Summary (threshold = 20%) ===", "", ""});
            writer.writeNext(new String[]{"Records > Threshold (Prosecutor)", pct(summary.getProsecutorRisk().getRecordsAtRisk()), "Fraction of records exceeding 20% re-id risk (prosecutor)"});
            writer.writeNext(new String[]{"Records > Threshold (Journalist)", pct(summary.getJournalistRisk().getRecordsAtRisk()), "Fraction of records exceeding 20% re-id risk (journalist)"});
            writer.writeNext(new String[]{"Success Rate > Threshold (Marketer)", pct(summary.getMarketerRisk().getSuccessRate()), "Overall marketer success rate (no per-record threshold)"});

            writer.flush();
        }

        return baos.toByteArray();
    }

    private String pct(double value) {
        return String.format("%.4f%%", value * 100);
    }
}