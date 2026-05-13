package web.api.infrastructure.client.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import web.api.infrastructure.client.properties.PresidioProperties;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PresidioClient {

    private final WebClient webClient;
    private final PresidioProperties presidioProperties;

    public String anonymizeText(String text) {
        List<Map<String, Object>> analyzerResults = webClient.post()
                .uri(presidioProperties.getAnalyzerUrl() + "/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "text", text,
                        "language", "en"
                ))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();

        assert analyzerResults != null;
        return webClient.post()
                .uri(presidioProperties.getAnonymizerUrl() + "/anonymize")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "text", text,
                        "analyzer_results", analyzerResults
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

