package com.NotificationService.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.UUID;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "sms_requests")
@Document(indexName = "sms")
public class SmsRequest {

  @PrePersist
  private void onCreate() {
    createdAt = new Date();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  @Field(type = FieldType.Keyword)
  private Long id;

  @Column(name = "correlation_id", unique = true)
  @Field(type = FieldType.Keyword)
  private UUID correlationID;

  @Column(name = "phone_number", nullable = false)
  @NotBlank(message = "Phone number must not be empty")
  @Field(type = FieldType.Keyword)
  private String phoneNumber;

  @Column(name = "message")
  @Field(type = FieldType.Text)
  private String message;

  @Column(name = "status")
  @Field(type = FieldType.Text)
  private String status;

  @Column(name = "failure_code")
  @Field(type = FieldType.Text)
  private String failureCode;

  @Column(name = "failure_comment")
  @Field(type = FieldType.Text)
  private String failureComment;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_At")
  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction)
  private Date createdAt;

  @Column(name = "updated_At")
  @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_fraction)
  private Date updatedAt;

  public SmsRequest(String phoneNumber, String message) {
    this.phoneNumber = phoneNumber;
    this.message = message;
    this.correlationID = UUID.randomUUID();
  }
}
