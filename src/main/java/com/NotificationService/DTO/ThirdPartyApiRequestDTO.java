package com.NotificationService.DTO;

import com.NotificationService.Model.DeliveryChannel;
import com.NotificationService.Model.Destination;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyApiRequestDTO {
  @JsonProperty("deliverychannel")
  private DeliveryChannel deliveryChannel;

  private Map<String, Object> channels;
  private List<Destination> destination;
}
