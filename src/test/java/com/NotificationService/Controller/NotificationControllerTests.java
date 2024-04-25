package com.NotificationService.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.NotificationService.Constants.Constants;
import com.NotificationService.DTO.BlackListRequestDTO;
import com.NotificationService.DTO.SmsRequestDTO;
import com.NotificationService.DTO.SmsSearchRequestDTO;
import com.NotificationService.Kafka.KafkaProducer;
import com.NotificationService.Model.BlackListNumber;
import com.NotificationService.Model.Sms;
import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Service.BlackListService;
import com.NotificationService.Service.ElasticSearchService;
import com.NotificationService.Service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = com.NotificationService.Controller.NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class NotificationControllerTests {
  @Autowired private MockMvc mockMvc;
  @MockBean private RequestService requestService;
  @MockBean private BlackListService blackListService;
  @MockBean private ElasticSearchService elasticSearchService;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private KafkaProducer kafkaProducer;

  @Test
  public void fetchSmsByIdTest() throws Exception {
    Long requestId = 1L;
    SmsRequest smsRequest = new SmsRequest("+911234567891", "Test Message");
    when(requestService.getSmsRequestById(requestId)).thenReturn(Optional.of(smsRequest));

    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.get(
                    Constants.ENDPOINT + Constants.ENDPOINT_FETCH_SMS_BY_ID, requestId)
                .contentType(MediaType.APPLICATION_JSON));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.phoneNumber").value("+911234567891"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value("Test Message"));
  }

  @Test
  public void addBlackListTest() throws Exception {
    BlackListRequestDTO requestDTO =
        new BlackListRequestDTO(Arrays.asList(new BlackListNumber("+911234567891")));
    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.post(Constants.ENDPOINT + Constants.ENDPOINT_BLACKLIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"));
  }

  @Test
  public void fetchBlackList() throws Exception {
    List<BlackListNumber> mockResult =
        Arrays.asList(new BlackListNumber("+911234567890"), new BlackListNumber("+910987654321"));
    when(blackListService.fetchAll()).thenReturn(mockResult);

    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.get(Constants.ENDPOINT + Constants.ENDPOINT_BLACKLIST)
                .contentType(MediaType.APPLICATION_JSON));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(mockResult.size()));
  }

  @Test
  public void deleteBlackList() throws Exception {
    List<BlackListNumber> blackListNumbers =
        Arrays.asList(new BlackListNumber("+911234567890"), new BlackListNumber("+910987654321"));
    BlackListRequestDTO blackListRequestDTO = new BlackListRequestDTO(blackListNumbers);
    doNothing().when(blackListService).deleteBlackList(any());

    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.delete(Constants.ENDPOINT + Constants.ENDPOINT_BLACKLIST)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(blackListRequestDTO)));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Successfully whitelisted"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"));
  }

  @Test
  public void sendSmsTest() throws Exception {
    SmsRequestDTO smsRequestDTO = new SmsRequestDTO("+911234567891", "Test Message");
    when(blackListService.isBlackListed(any(BlackListNumber.class))).thenReturn(false);

    SmsRequest smsRequest = new SmsRequest();
    smsRequest.setCorrelationID(UUID.randomUUID());
    when(requestService.addSmsRequest(any(SmsRequest.class))).thenReturn(smsRequest);

    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.post(Constants.ENDPOINT + Constants.SEND_SMS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(smsRequestDTO)));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SUCCESS"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments").value("Successfully Sent"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.data.requestId".toString())
                .value(smsRequest.getCorrelationID().toString()));
  }

  @Test
  public void getSmsByPhoneNumberAndTimeTest() throws Exception {
    SmsSearchRequestDTO searchRequestDTO = new SmsSearchRequestDTO();
    searchRequestDTO.setPhoneNumber("+911234567891");
    searchRequestDTO.setStartTime(new Date());
    searchRequestDTO.setEndTime(new Date());

    List<SmsRequestDTO> smsRequests =
        Arrays.asList(new SmsRequestDTO("+911234567891", "Test Message"));

    List<SmsRequest> smsRequestsResponse =
        Arrays.asList(new SmsRequest("+911234567891", "Test Message"));

    when(elasticSearchService.getSmsByPhoneNumberAndTime(
            searchRequestDTO.getPhoneNumber(),
            searchRequestDTO.getStartTime(),
            searchRequestDTO.getEndTime(),
            PageRequest.of(0, 10)))
        .thenReturn(smsRequestsResponse);

    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.get(Constants.ENDPOINT + Constants.FILTER_BY_PHONE_AND_TIME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequestDTO)));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phoneNumber").value("+911234567891"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].message").value("Test Message"));
  }

  @Test
  public void getSmsByMessageTest() throws Exception {
    Sms sms = new Sms();
    sms.setText("This is a Test Message");

    List<SmsRequestDTO> smsRequests =
        Arrays.asList(new SmsRequestDTO("+911234567891", "Test Message"));

    List<SmsRequest> smsRequestsResponse =
        Arrays.asList(new SmsRequest("+911234567891", "Test Message"));

    when(elasticSearchService.getSmsByText(sms.getText(), PageRequest.of(0, 10)))
        .thenReturn(smsRequestsResponse);

    ResultActions response =
        mockMvc.perform(
            MockMvcRequestBuilders.get(Constants.ENDPOINT + Constants.FILTER_BY_TEXT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sms)));
    response
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phoneNumber").value("+911234567891"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].message").value("Test Message"));
  }
}
