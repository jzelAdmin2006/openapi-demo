package com.jzel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class ApiResponse {

  @SerializedName("choices")
  private Choice[] choices;

  @Data
  public static class Choice {

    @SerializedName("message")
    private Message message;
  }

  @Data
  public static class Message {

    @SerializedName("content")
    private String content;
  }
}
