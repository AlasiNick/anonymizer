package deidentification.arx.web;

import deidentification.arx.service.ArxRiskAssessmentService;
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
    private final ArxRiskAssessmentService riskAssessmentService;

    @PostMapping(value = "/anonymize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> anonymize(@RequestParam("file") MultipartFile file) throws Exception {
        byte[] anonymized = service.anonymize(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=anonymized.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(anonymized);
    }

    @PostMapping(value = "/risk-assessment", produces = "text/csv")
    public ResponseEntity<byte[]> riskAssessment(@RequestParam("file") MultipartFile file) {
        try {
            byte[] csvBytes = file.getBytes();
            byte[] riskReport = riskAssessmentService.performRiskAssessment(csvBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=risk_assessment_report.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(riskReport);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
