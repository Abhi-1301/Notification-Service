package com.NotificationService.Kafka;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class KafkaTemplateEntity {
  private UUID correlationId;
}
