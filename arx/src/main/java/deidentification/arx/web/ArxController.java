package deidentification.arx.web;

import deidentification.arx.service.DeIdentificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/arx")
@RequiredArgsConstructor
public class ArxController {

    private final DeIdentificationService service;

    @PostMapping(value = "/anonymize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> anonymize(@RequestParam("file") MultipartFile file) throws Exception {
        byte[] anonymized = service.anonymize(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=anonymized.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(anonymized);
    }
}
