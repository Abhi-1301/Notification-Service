package com.NotificationService.Service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.NotificationService.Model.BlackListNumber;
import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Service.Impl.SendSmsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class SendSmsServiceTests {
  @Mock private RequestService requestService;

  @Mock private BlackListService blackListService;

  @Mock private ElasticSearchService elasticSearchService;

  @Mock private ObjectMapper objectMapper;

  @Mock private RestTemplate restTemplate;

  @InjectMocks private SendSmsServiceImpl sendSmsService;

  @Test
  public void consumeRequestIdTest() throws Exception {
    SmsRequest smsRequest = new SmsRequest();
    smsRequest.setPhoneNumber("+911234567891");
    smsRequest.setCorrelationID(UUID.randomUUID());

    when(requestService.getSmsRequestByCorrelationId(any(UUID.class)))
        .thenReturn(Optional.of(smsRequest));
    when(blackListService.isBlackListed(any(BlackListNumber.class))).thenReturn(false);

    ResponseEntity<String> responseEntity = new ResponseEntity<>("responseBody", HttpStatus.OK);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("response", new LinkedHashMap<>());

    sendSmsService.consumeRequestId(smsRequest.getCorrelationID());
    
    verify(requestService, times(1)).getSmsRequestByCorrelationId(any(UUID.class));
    verify(blackListService, times(1)).isBlackListed(any(BlackListNumber.class));
    verify(elasticSearchService, times(1)).saveSmsRequest(eq(smsRequest));
    verify(requestService, times(1)).updateSmsRequest(eq(smsRequest));
  }
}
