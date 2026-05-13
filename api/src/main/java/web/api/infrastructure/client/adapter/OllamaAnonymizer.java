package web.api.infrastructure.client.adapter;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OllamaAnonymizer {

    private static final String SYSTEM_PROMPT = """
            You are an expert medical data anonymizer specializing in Estonian clinical documents.
            Your task is to replace all personally identifiable information (PII) with standardized placeholders.
            
            Rules:
            - Person names → <PERSON>
            - Dates and times → <DATE_TIME>
            - Phone numbers → <PHONE_NUMBER>
            - Locations, cities, hospitals → <LOCATION>
            - IDs, driver licenses, codes → <ID>
            - Keep all medical terminology and sentence structure intact.
            - Return ONLY the anonymized text. No explanations, no extra text.
            """;

    private final ChatClient chatClient;

    public OllamaAnonymizer(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public String anonymizeText(String originalText) {
        if (originalText == null || originalText.trim().isEmpty()) {
            return originalText;
        }

        return chatClient.prompt()
                .user(originalText)
                .call()
                .content();
    }
}