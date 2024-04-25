package com.NotificationService.DTO;

import com.NotificationService.Model.BlackListNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BlackListRequestDTO {
  private List<BlackListNumber> phoneNumbers;
}
