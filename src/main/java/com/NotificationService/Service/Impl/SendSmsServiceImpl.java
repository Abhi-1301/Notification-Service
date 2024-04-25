package com.NotificationService.Service.Impl;

import com.NotificationService.Constants.Constants;
import com.NotificationService.DTO.*;
import com.NotificationService.Model.*;
import com.NotificationService.Service.BlackListService;
import com.NotificationService.Service.ElasticSearchService;
import com.NotificationService.Service.RequestService;
import com.NotificationService.Service.SendSmsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class SendSmsServiceImpl implements SendSmsService {
  @Autowired RequestService requestService;
  @Autowired BlackListService blackListService;
  @Autowired ElasticSearchService elasticSearchService;
  @Autowired ObjectMapper objectMapper;

  public List<ThirdPartyApiRequestDTO> buildRequestBody(SmsRequest smsRequest) {
    ThirdPartyApiRequestDTO thirdPartyApiRequestDTO = new ThirdPartyApiRequestDTO();
    thirdPartyApiRequestDTO.setDeliveryChannel(DeliveryChannel.SMS);

    Map<String, Object> channels = new HashMap<>();
    channels.put("sms", new Sms(smsRequest.getMessage())); // enum for channels
    thirdPartyApiRequestDTO.setChannels(channels);

    Destination destination = new Destination();
    destination.setMsisdn(List.of(smsRequest.getPhoneNumber()));
    destination.setCorrelationId(UUID.randomUUID().toString());
    thirdPartyApiRequestDTO.setDestination(List.of(destination));

    try {
      String requestBodyJson = objectMapper.writeValueAsString(List.of(thirdPartyApiRequestDTO));
      System.out.println(requestBodyJson);
    } catch (Exception e) {
      throw new RuntimeException();
    }

    return List.of(thirdPartyApiRequestDTO);
  }

  private void updateSmsResponseDetails(
      SmsRequest smsRequest, ArrayList<LinkedHashMap> responseBody) {
    try {
      LinkedHashMap<String, String> result = responseBody.get(0);
      smsRequest.setStatus(result.get("description"));
      smsRequest.setUpdatedAt(new Date());
      requestService.updateSmsRequest(smsRequest);
      elasticSearchService.saveSmsRequest(smsRequest);
    } catch (IndexOutOfBoundsException e) {
      throw new RuntimeException();
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  private void updateSmsResponseDetails(SmsRequest smsRequest, LinkedHashMap responseBody) {
    try {
      smsRequest.setFailureComment((String) responseBody.get("description"));
      smsRequest.setFailureCode((String) responseBody.get("code"));
      smsRequest.setUpdatedAt(new Date());
      requestService.updateSmsRequest(smsRequest);
      elasticSearchService.saveSmsRequest(smsRequest);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @SneakyThrows
  public Map<String, Object> fetchResponse(ResponseEntity<String> responseEntity) {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestResponse = responseEntity.getBody();
    return objectMapper.readValue(requestResponse, HashMap.class);
  }

  @Override
  public void consumeRequestId(UUID correlationId) throws Exception {
    SmsRequest smsRequest =
        requestService
            .getSmsRequestByCorrelationId(correlationId)
            .orElseThrow(() -> new RuntimeException());
    String phoneNumber = smsRequest.getPhoneNumber();
    if (blackListService.isBlackListed(new BlackListNumber(phoneNumber))) {
      return;
    } else {
      String apiEndpoint = Constants.API_ENDPOINT;
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("key", Constants.THIRD_PARTY_API_KEY);

      RestTemplate restTemplate = new RestTemplate();
      HttpEntity<List<ThirdPartyApiRequestDTO>> requestEntity =
          new HttpEntity<>(buildRequestBody(smsRequest), headers);
      ResponseEntity<String> responseEntity =
          restTemplate.postForEntity(apiEndpoint, requestEntity, String.class);
      Map<String, Object> result = fetchResponse(responseEntity);
      Object response = result.get("response");
      if (response.getClass().equals(LinkedHashMap.class))
        updateSmsResponseDetails(smsRequest, (LinkedHashMap) response);
      else updateSmsResponseDetails(smsRequest, (ArrayList<LinkedHashMap>) response);
    }
  }
}
