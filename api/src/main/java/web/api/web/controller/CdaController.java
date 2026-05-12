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

    @PostMapping("/csv")
    public ResponseEntity<byte[]> getUnmappedFieldsCsv(@Valid @RequestBody UploadXmlRequest request) throws Exception {
        byte[] csvContent = cdaRemapService.generateUnmappedFieldsCsv(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("unmapped_fields.csv")
                                .build().toString())
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csvContent);
    }
}
