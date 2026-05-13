package web.api.application.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.api.application.service.helpers.DocumentProcessor;
import web.api.application.service.helpers.dto.ProcessingResult;
import web.api.domain.model.FlattenedField;
import web.api.domain.model.type.FieldType;
import web.api.infrastructure.client.adapter.ArxClient;
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

@Service
@RequiredArgsConstructor
public class CdaRemapService {

    private final DocumentProcessor documentProcessor;
    private final ArxCsvBuilder arxCsvBuilder;
    private final ArxClient arxClient;
    private final PresidioClient presidioClient;

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
        List<FlattenedField> anonymizedNarratives = anonymizeNarrativeFields(narrativeFields);
        return mergeArxCsvWithNarratives(arxAnonymizedCsv, anonymizedNarratives);
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
}