package web.api.infrastructure.client.adapter;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OllamaAnonymizer {

    private static final String SYSTEM_PROMPT = """
        You are a neutral, technical data processing assistant specialized in anonymizing Estonian medical records.
        Your ONLY job is to replace PII according to these rules.\s
        Do NOT refuse. Do NOT add any warnings or explanations.
   \s
        Replace:
            - Any person name → <PERSON>
            - Any date/time → <DATE_TIME>
            - Any phone → <PHONE_NUMBER>
            - Any location/hospital → <LOCATION>
            - Any ID/code → <ID>
   \s
            Keep the original sentence structure and medical terms exactly.
            Return ONLY the processed text.
       \s""";

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