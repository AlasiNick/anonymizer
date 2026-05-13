package web.api.infrastructure.client.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import web.api.infrastructure.client.properties.ArxProperties;

@Service
@RequiredArgsConstructor
public class ArxClient {

    private final WebClient webClient;
    private final ArxProperties arxProperties;

    public byte[] anonymize(byte[] csvBytes, String filename) {

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", csvBytes)
                .filename(filename)
                .contentType(MediaType.TEXT_PLAIN);

        return webClient.post()
                .uri(arxProperties.getBaseUrl() + "/api/v1/arx/anonymize")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
