package com.NotificationService.Kafka;

import com.NotificationService.Constants.Constants;
import com.NotificationService.Service.SendSmsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaConsumer {
  @Autowired SendSmsService sendSmsService;
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

  @KafkaListener(topics = Constants.KAFKA_TOPIC_NOTIFICATION_SEND_SMS, groupId = Constants.KAFKA_GROUP_ID)
  public void consumer(ConsumerRecord<String, KafkaTemplateEntity> consumerRecord)
      throws Exception {
    try {
      String key = consumerRecord.key();
      UUID correlationId = consumerRecord.value().getCorrelationId();
      sendSmsService.consumeRequestId(correlationId);
    } catch (Exception e) {
      throw new Exception("Failed to consume message", e);
    }
  }
}
