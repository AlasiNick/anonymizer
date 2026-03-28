package deidentification.api;
import deidentification.api.service.DeIdentificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ArxTestRunner implements CommandLineRunner {
    private final DeIdentificationService deidService;

    public ArxTestRunner(DeIdentificationService deidService) {
        this.deidService = deidService;
    }

    @Override
    public void run(String... args) {
        try {
            deidService.anonymizeExample();
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}