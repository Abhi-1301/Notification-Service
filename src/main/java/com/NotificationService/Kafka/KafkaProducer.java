package com.NotificationService.Kafka;

import com.NotificationService.Constants.Constants;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

  @Autowired private KafkaTemplate<String, KafkaTemplateEntity> kafkaTemplate;

  public void sendRequestId(KafkaTemplateEntity kafkaTemplateEntity) throws Exception {
    try {
      String topic = Constants.KAFKA_TOPIC_NOTIFICATION_SEND_SMS;
      String key = "request-id";
      System.out.println ("SENT");
      ProducerRecord<String, KafkaTemplateEntity> record =
          new ProducerRecord<>(topic, key, kafkaTemplateEntity);
      this.kafkaTemplate.send(record);
    } catch (Exception e) {
    }
  }
}
