package web.api.application.service;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import web.api.domain.model.FlattenedField;
import web.api.domain.model.type.FieldType;
import web.api.infrastructure.client.adapter.ArxClient;
import web.api.infrastructure.mapper.XmlFlattener;
import web.api.infrastructure.parser.SecureXmlParser;
import web.api.infrastructure.client.builder.ArxCsvBuilder;
import web.api.infrastructure.parser.XmlValidationService;
import web.api.web.dto.request.UploadXmlRequest;
import web.api.web.dto.response.FieldResponse;
import web.api.web.dto.response.FlattenedResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CdaRemapService {

    private final XmlValidationService validationService;
    private final XmlFlattener xmlFlattener;
    private final ArxCsvBuilder arxCsvBuilder;
    private final ArxClient arxClient;

    public FlattenedResponse process(UploadXmlRequest request) throws Exception {
        validationService.validateExtension(request.getFileName());
        byte[] xmlBytes = validationService.validateAndDecodeBase64(request.getBase64Content());
        validationService.validateXml(xmlBytes);

        Document document = SecureXmlParser.createDocument(xmlBytes);
        List<FlattenedField> fields = xmlFlattener.flatten(document);

        List<FieldResponse> fieldResponses = fields.stream()
                .map(this::mapToFieldResponse)
                .toList();

        FlattenedResponse response = new FlattenedResponse();
        response.setFields(fieldResponses);
        return response;
    }

    public byte[] generateUnmappedFieldsCsv(UploadXmlRequest request) throws Exception {
        validationService.validateExtension(request.getFileName());
        byte[] xmlBytes = validationService.validateAndDecodeBase64(request.getBase64Content());
        validationService.validateXml(xmlBytes);

        Document document = SecureXmlParser.createDocument(xmlBytes);
        List<FlattenedField> fields = xmlFlattener.flatten(document);

        return generateCsv(fields);
    }

    public byte[] prepareStructuredCsvForArx(UploadXmlRequest request) throws Exception {
        validationService.validateExtension(request.getFileName());
        byte[] xmlBytes = validationService.validateAndDecodeBase64(request.getBase64Content());
        validationService.validateXml(xmlBytes);

        Document document = SecureXmlParser.createDocument(xmlBytes);
        List<FlattenedField> allFields = xmlFlattener.flatten(document);

        List<FlattenedField> structured = filterStructuredFields(allFields);
        return arxCsvBuilder.generateArxReadyCsv(structured);
    }

    public byte[] processAndAnonymize(UploadXmlRequest request) throws Exception {
        byte[] csvForArx = prepareStructuredCsvForArx(request);
        return arxClient.anonymize(csvForArx, "anonymized.csv");
    }

    private byte[] generateCsv(List<FlattenedField> fields) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(osw)) {

            // UTF-8 BOM for Excel
            baos.write(0xEF); baos.write(0xBB); baos.write(0xBF);

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

    private List<FlattenedField> filterStructuredFields(List<FlattenedField> fields) {
        return fields.stream()
                .filter(field ->
                        field.getType() != FieldType.UNKNOWN &&
                                field.getType() != FieldType.NARRATIVE
                ).toList();
    }

    private FieldResponse mapToFieldResponse(FlattenedField field) {
        FieldResponse resp = new FieldResponse();
        resp.setPath(field.getDisplayPath());
        resp.setValue(field.getValue());
        resp.setType(field.getType());
        return resp;
    }
}