package web.api.application.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.api.application.service.helpers.DocumentProcessor;
import web.api.application.service.helpers.dto.ProcessingResult;
import web.api.domain.model.FlattenedField;
import web.api.domain.model.type.FieldType;
import web.api.infrastructure.client.adapter.ArxClient;
import web.api.infrastructure.client.adapter.OllamaAnonymizer;
import web.api.infrastructure.client.adapter.PresidioClient;
import web.api.infrastructure.client.builder.ArxCsvBuilder;
import web.api.infrastructure.client.builder.CsvRemapperBuilder;
import web.api.utility.CsvUtils;
import web.api.web.dto.request.UploadXmlRequest;
import web.api.web.dto.response.FieldResponse;
import web.api.web.dto.response.FlattenedResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class CdaRemapService {

    private final DocumentProcessor documentProcessor;
    private final ArxCsvBuilder arxCsvBuilder;
    private final ArxClient arxClient;
    private final PresidioClient presidioClient;
    private final OllamaAnonymizer ollamaAnonymizer;

    /**
     * MAIN LOGIC
     * @param request UploadXmlRequest
     * @return anonymized file
     * @throws Exception ex
     */
    public byte[] processHybridAnonymization(UploadXmlRequest request) throws Exception {
        ProcessingResult result = documentProcessor.process(request);
        List<FlattenedField> allFields = result.fields();

        List<FlattenedField> structuredFields = filterStructuredFields(allFields);
        byte[] structuredCsv = arxCsvBuilder.generateArxReadyCsv(structuredFields);
        byte[] arxAnonymizedCsv = arxClient.anonymize(structuredCsv, "structured.csv");

        List<FlattenedField> narrativeFields = filterNarrativeFields(allFields);
        List<FlattenedField> presidioAnonymized = anonymizeNarrativeFields(narrativeFields);

        List<FlattenedField> finalNarratives = applyOllamaRefinement(presidioAnonymized);
        byte[] finalAnonymizedCsv = mergeArxCsvWithNarratives(arxAnonymizedCsv, finalNarratives);
        byte[] riskReport = arxClient.riskAssessment(finalAnonymizedCsv, "risk_input.csv");

        return createZipWithBothFiles(finalAnonymizedCsv, riskReport);
    }

    public FlattenedResponse process(UploadXmlRequest request) throws Exception {
        ProcessingResult result = documentProcessor.process(request);
        List<FieldResponse> responses = result.fields().stream()
                .map(this::mapToFieldResponse)
                .toList();
        FlattenedResponse response = new FlattenedResponse();
        response.setFields(responses);
        return response;
    }

    public byte[] generateUnmappedFieldsCsv(UploadXmlRequest request) throws Exception {
        ProcessingResult result = documentProcessor.process(request);
        return generateCsv(result.fields());
    }

    public byte[] prepareStructuredCsvForArx(UploadXmlRequest request) throws Exception {
        ProcessingResult result = documentProcessor.process(request);
        List<FlattenedField> structured = filterStructuredFields(result.fields());
        return arxCsvBuilder.generateArxReadyCsv(structured);
    }

    public byte[] processAndAnonymize(UploadXmlRequest request) throws Exception {
        byte[] csvForArx = prepareStructuredCsvForArx(request);
        return arxClient.anonymize(csvForArx, "anonymized.csv");
    }

    private byte[] createZipWithBothFiles(byte[] anonymizedCsv, byte[] riskReport) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry anonymizedEntry = new ZipEntry("anonymized_data.csv");
            zos.putNextEntry(anonymizedEntry);
            zos.write(anonymizedCsv);
            zos.closeEntry();
            ZipEntry riskEntry = new ZipEntry("risk_assessment_report.csv");
            zos.putNextEntry(riskEntry);
            zos.write(riskReport);
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        }
    }

    private List<FlattenedField> applyOllamaRefinement(List<FlattenedField> presidioFields) {
        return presidioFields.stream()
                .map(field -> {
                    String cleanText = extractCleanText(field.getValue());

                    try {
                        String ollamaResult = ollamaAnonymizer.anonymizeText(cleanText);

                        if (isRefusalResponse(ollamaResult)) {
                            System.out.println("Ollama refused for field: " + field.getCleanPath() + " → using Presidio version");
                            return field;
                        }

                        return withValue(field, ollamaResult);

                    } catch (Exception e) {
                        System.err.println("Ollama failed for " + field.getCleanPath() + ": " + e.getMessage());
                        return field;
                    }
                })
                .toList();
    }

    private boolean isRefusalResponse(String text) {
        if (text == null) return true;
        String lower = text.toLowerCase();
        return lower.contains("can't fulfill") ||
                lower.contains("cannot fulfill") ||
                lower.contains("i cannot") ||
                lower.contains("i can't") ||
                lower.contains("refuse") ||
                lower.contains("safety") ||
                lower.contains("self-harm") ||
                lower.contains("personal data");
    }

    private FlattenedField withValue(FlattenedField original, String newValue) {
        FlattenedField copy = new FlattenedField();
        copy.setOriginalPath(original.getOriginalPath());
        copy.setCleanPath(original.getCleanPath());
        copy.setType(original.getType());
        copy.setValue(newValue);
        return copy;
    }

    private byte[] mergeArxCsvWithNarratives(byte[] arxAnonymizedCsv, List<FlattenedField> anonymizedNarratives) {
        List<String[]> arxRows = CsvRemapperBuilder.read(arxAnonymizedCsv);
        if (arxRows.isEmpty() || arxRows.size() < 2) {
            return arxCsvBuilder.generateArxReadyCsv(anonymizedNarratives);
        }

        String[] arxHeader = arxRows.get(0);
        String[] arxValues = arxRows.get(1);
        List<String> finalColumns = new ArrayList<>(Arrays.asList(arxHeader));
        List<FlattenedField> narrativesToAdd = anonymizedNarratives.stream()
                .filter(n -> !finalColumns.contains(n.getCleanPath()))
                .map(this::withCleanText)
                .toList();

        finalColumns.addAll(narrativesToAdd.stream()
                .map(FlattenedField::getCleanPath)
                .toList());

        Map<String, String> finalValueMap = new LinkedHashMap<>();
        for (int i = 0; i < arxHeader.length; i++) {
            finalValueMap.put(arxHeader[i], arxValues[i]);
        }

        for (FlattenedField nf : narrativesToAdd) {
            finalValueMap.put(nf.getCleanPath(), nf.getValue());
        }

        return buildHorizontalCsv(finalColumns, finalValueMap);
    }

    private byte[] buildHorizontalCsv(List<String> columns, Map<String, String> valueMap) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            CsvUtils.writeBom(baos);
            writer.writeNext(columns.toArray(new String[0]));
            String[] row = columns.stream()
                    .map(col -> valueMap.getOrDefault(col, ""))
                    .toArray(String[]::new);

            writer.writeNext(row);
            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build final horizontal CSV", e);
        }
    }

    private List<FlattenedField> anonymizeNarrativeFields(List<FlattenedField> narrative) {
        return narrative.stream()
                .map(this::anonymizeSingleField)
                .toList();
    }

    private FlattenedField anonymizeSingleField(FlattenedField f) {
        String masked = presidioClient.anonymizeText(f.getValue());
        FlattenedField copy = new FlattenedField();
        copy.setOriginalPath(f.getOriginalPath());
        copy.setCleanPath(f.getCleanPath());
        copy.setType(f.getType());
        copy.setValue(masked);
        return copy;
    }

    private List<FlattenedField> filterStructuredFields(List<FlattenedField> fields) {
        return fields.stream()
                .filter(f -> f.getType() != FieldType.UNKNOWN && f.getType() != FieldType.NARRATIVE)
                .toList();
    }

    private List<FlattenedField> filterNarrativeFields(List<FlattenedField> fields) {
        return fields.stream()
                .filter(f -> f.getType() == FieldType.NARRATIVE)
                .toList();
    }

    private FieldResponse mapToFieldResponse(FlattenedField field) {
        FieldResponse resp = new FieldResponse();
        resp.setPath(field.getDisplayPath());
        resp.setValue(field.getValue());
        resp.setType(field.getType());
        return resp;
    }

    private byte[] generateCsv(List<FlattenedField> fields) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(osw)) {
            CsvUtils.writeBom(baos);
            csvWriter.writeNext(new String[]{"Path", "OriginalPath", "Value", "Type", "IsUnmapped"});

            for (FlattenedField f : fields) {
                boolean unmapped = f.getType() == FieldType.UNKNOWN;
                csvWriter.writeNext(new String[]{
                        f.getDisplayPath(),
                        f.getOriginalPath(),
                        f.getValue(),
                        f.getType().name(),
                        unmapped ? "YES" : "NO"
                });
            }
            csvWriter.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }

    private FlattenedField withCleanText(FlattenedField original) {
        FlattenedField copy = new FlattenedField();
        copy.setOriginalPath(original.getOriginalPath());
        copy.setCleanPath(original.getCleanPath());
        copy.setType(original.getType());
        copy.setValue(extractCleanText(original.getValue()));
        return copy;
    }

    private String extractCleanText(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return rawValue;
        }

        if (!rawValue.trim().startsWith("{")) {
            return rawValue;
        }

        try {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "\"text\"\\s*:\\s*\"(.*?)\"", java.util.regex.Pattern.DOTALL);
            java.util.regex.Matcher matcher = pattern.matcher(rawValue);

            if (matcher.find()) {
                String text = matcher.group(1);
                return text.replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }
        } catch (Exception _) {}

        return rawValue;
    }
}