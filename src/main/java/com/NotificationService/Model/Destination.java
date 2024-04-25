package com.NotificationService.Model;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Destination {
  private List<String> msisdn;
  private String correlationId;
}
