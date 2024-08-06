package com.jzel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class Main {

  private static final String OPENAPI_KEY = System.getenv("OPENAPI_KEY");
  private static final String MODEL = "gpt-4o-mini";
  private static final Gson GSON = new Gson();
  private static final HttpClientResponseHandler<String> RESPONSE_HANDLER = response -> {
    final int status = response.getCode();
    if (status >= 200 && status < 300) {
      return GSON.fromJson(readResponse(response), ApiResponse.class).getChoices()[0].getMessage().getContent();
    } else {
      throw new IOException("Unexpected response status: " + status);
    }
  };

  public static void main(final String[] args) {
    System.out.println(promptLLM("Please give me an interesting answer to my OpenAPI demo project prompt."));
  }

  private static String promptLLM(final String message) {
    try (final CloseableHttpClient httpClient = HttpClients.createDefault()) {
      return httpClient.execute(createRequest(message), RESPONSE_HANDLER);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static HttpPost createRequest(final String message) {
    final HttpPost request = new HttpPost("https://api.openai.com/v1/chat/completions");
    request.setHeader("Authorization", "Bearer " + OPENAPI_KEY);
    request.setHeader("Content-Type", "application/json");
    request.setEntity(createRequestEntity(message));
    return request;
  }

  private static StringEntity createRequestEntity(final String message) {
    return new StringEntity(
        """
            {
              "model": "%s",
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(MODEL, message),
        UTF_8
    );
  }

  private static String readResponse(ClassicHttpResponse response) throws IOException {
    try (final BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), UTF_8))) {
      return r.lines().collect(joining("\n"));
    }
  }
}
