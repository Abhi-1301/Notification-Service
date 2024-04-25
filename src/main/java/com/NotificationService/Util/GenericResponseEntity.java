package com.NotificationService.Util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;



@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponseEntity<R> {
  @JsonProperty("success")
  private boolean successStatus;

  @JsonProperty("message")
  private String message;

  @JsonProperty("data")
  private R data;

  @JsonProperty("error")
  private ErrorResponse error;

  public static <R> GenericResponseEntity successResponse(R data, String message) {
    return GenericResponseEntity.<R>builder()
        .successStatus(true)
        .message(message)
        .data(data)
        .build();
  }

  public static <R> GenericResponseEntity errorResponse(R data, String message) {
    return GenericResponseEntity.<R>builder()
        .successStatus(false)
        .message(message)
        .data(data)
        .build();
  }
}
