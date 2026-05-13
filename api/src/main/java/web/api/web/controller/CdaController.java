package web.api.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.api.application.service.CdaRemapService;
import web.api.web.dto.request.UploadXmlRequest;
import web.api.web.dto.response.FlattenedResponse;

@RestController
@RequestMapping("/api/v1/cda")
@RequiredArgsConstructor
public class CdaController {

    private final CdaRemapService cdaRemapService;

    @PostMapping("/flatten")
    public FlattenedResponse flatten(@Valid @RequestBody UploadXmlRequest request) throws Exception {
        return cdaRemapService.process(request);
    }

    @PostMapping(value = "/unmapped-csv", produces = "text/csv")
    public ResponseEntity<byte[]> unmappedCsv(@RequestBody UploadXmlRequest request) throws Exception {
        byte[] csv = cdaRemapService.generateUnmappedFieldsCsv(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=unmapped-fields.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @PostMapping(value = "/arx-ready-csv", produces = "text/csv")
    public ResponseEntity<byte[]> arxReadyCsv(@RequestBody UploadXmlRequest request) throws Exception {
        byte[] csv = cdaRemapService.prepareStructuredCsvForArx(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=arx-ready.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @PostMapping(value = "/anonymize", produces = "text/csv")
    public ResponseEntity<byte[]> anonymize(@RequestBody UploadXmlRequest request) throws Exception {
        byte[] anonymized = cdaRemapService.processAndAnonymize(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=anonymized.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(anonymized);
    }
}
