package com.trueque_api.staff.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.trueque_api.staff.dto.AiListingResponseDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Component
public class OpenAiClient {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api-key}")
    private String apiKey;

    public AiListingResponseDTO analyzeImage(String base64Image) throws IOException {

        String prompt = """
        You are helping fill a marketplace listing.

        From the image, return a JSON with:
        - title
        - description
        - category (one word)
        - condition (New or Used)

        Respond ONLY with valid JSON.
        """;

        Map<String, Object> payload = Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                Map.of(
                    "role", "user",
                    "content", List.of(
                        Map.of("type", "text", "text", prompt),
                        Map.of("type", "image_url",
                               "image_url", Map.of("url", base64Image))
                    )
                )
            )
        );

        Request request = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(
                mapper.writeValueAsString(payload),
                MediaType.parse("application/json")
            ))
            .build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();

            String json = mapper
                .readTree(body)
                .at("/choices/0/message/content")
                .asText();

            return mapper.readValue(json, AiListingResponseDTO.class);
        }
    }
}