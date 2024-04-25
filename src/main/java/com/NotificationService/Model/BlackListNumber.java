package com.NotificationService.Model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "blackListNumbers")
public class BlackListNumber implements Serializable {
  @Id
  @Indexed
  private String phoneNumber;
}
