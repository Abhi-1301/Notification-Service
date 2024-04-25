package com.NotificationService.Service;

import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Repository.RequestRepository;
import com.NotificationService.Service.Impl.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {
  @Mock private RequestRepository requestRepository;
  @InjectMocks private RequestServiceImpl requestService;

  @BeforeEach
  void setUp() {
    reset(requestRepository);
  }

  @Test
  void addSmsRequestTest() throws Exception {
    SmsRequest smsRequest = new SmsRequest();
    SmsRequest savedSmsRequest = new SmsRequest();
    when(requestRepository.save(smsRequest)).thenReturn(savedSmsRequest);

    SmsRequest result = requestService.addSmsRequest(smsRequest);

    assertEquals(savedSmsRequest, result);
    verify(requestRepository, times(1)).save(smsRequest);
  }

  @Test
  void getSmsRequestById() throws Exception {
    Long id = new Random().nextLong();
    SmsRequest smsRequest = new SmsRequest();
    when(requestRepository.findById(id)).thenReturn(Optional.of(smsRequest));

    Optional<SmsRequest> result = requestService.getSmsRequestById(id);
    assertTrue(result != null);
    assertEquals(Optional.of(smsRequest), result);
    verify(requestRepository, times(1)).findById(id);
  }

  @Test
  void updateSmsRequestTest() throws Exception {
    Long id = new Random().nextLong();
    SmsRequest smsRequest = new SmsRequest();
    when(requestRepository.findById(id)).thenReturn(Optional.of(smsRequest));

    Optional<SmsRequest> result = requestService.getSmsRequestById(id);
    assertTrue(result != null);
    assertEquals(Optional.of(smsRequest), result);
    verify(requestRepository, times(1)).findById(id);

    smsRequest.setId(new Random().nextLong());
    requestService.updateSmsRequest(smsRequest);
    result = requestService.getSmsRequestById(id);
    assertTrue(result != null);
    assertEquals(Optional.of(smsRequest), result);
    verify(requestRepository, times(2)).findById(id);
  }

  @Test
  public void getSmsRequestByCorrelationIdtest() throws Exception {
    UUID correlationId = UUID.randomUUID();
    SmsRequest smsRequest = new SmsRequest();
    when(requestRepository.findByCorrelationID(correlationId)).thenReturn(Optional.of(smsRequest));

    Optional<SmsRequest> result = requestService.getSmsRequestByCorrelationId(correlationId);

    assertEquals(Optional.of(smsRequest), result);
    verify(requestRepository, times(1)).findByCorrelationID(correlationId);
  }
}
