package com.looklog.service;

import com.looklog.entity.Tag;
import com.looklog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

// GeminiTagService.java
@Service
@RequiredArgsConstructor
public class GeminiTagService {

  private final TagRepository tagRepository;
  private final RestClient restClient = RestClient.create();

  @Value("${gemini.api.key}")
  private String apiKey;

  private static final String MODEL = "gemini-3.6-flash";

  public List<String> suggestTags(MultipartFile image) throws IOException {
    String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
    String mimeType = image.getContentType();

    List<String> allowedTags = tagRepository.findAll().stream()
            .map(Tag::getName)
            .toList();

    String prompt = """
            사진 속 옷차림을 보고 아래 태그 목록 중에서만 골라서
            어울리는 태그를 최대 6개 골라줘.
            반드시 JSON 배열 형식으로만 답해. 예: ["블랙","미니멀","가을"]
            다른 설명은 절대 붙이지 마.

            태그 목록: %s
            """.formatted(String.join(", ", allowedTags));

    Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                    "parts", List.of(
                            Map.of("text", prompt),
                            Map.of("inline_data", Map.of(
                                    "mime_type", mimeType,
                                    "data", base64Image
                            ))
                    )
            ))
    );

    String url = "https://generativelanguage.googleapis.com/v1beta/models/"
            + MODEL + ":generateContent?key=" + apiKey;

    try {
      Map<?, ?> response = restClient.post()
              .uri(url)
              .contentType(MediaType.APPLICATION_JSON)
              .body(requestBody)
              .retrieve()
              .body(Map.class);

      String text = extractText(response);
      List<String> aiTags = parseJsonArray(text);

      // 목록에 없는 태그는 걸러내기
      return aiTags.stream()
              .filter(allowedTags::contains)
              .toList();

    } catch (Exception e) {
      e.printStackTrace();
      return List.of(); // 실패해도 빈 리스트만 반환, 글쓰기는 정상 진행
    }
  }

  @SuppressWarnings("unchecked")
  private String extractText(Map<?, ?> response) {
    List<?> candidates = (List<?>) response.get("candidates");
    Map<?, ?> content = (Map<?, ?>) ((Map<?, ?>) candidates.get(0)).get("content");
    List<?> parts = (List<?>) content.get("parts");
    return (String) ((Map<?, ?>) parts.get(0)).get("text");
  }

  private List<String> parseJsonArray(String text) {
    try {
      String cleaned = text.replaceAll("```json|```", "").trim();
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(cleaned, new TypeReference<List<String>>() {});
    } catch (Exception e) {
      return List.of();
    }
  }
}