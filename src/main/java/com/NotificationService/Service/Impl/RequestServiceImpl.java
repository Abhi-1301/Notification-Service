package com.NotificationService.Service.Impl;

import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Repository.RequestRepository;
import com.NotificationService.Service.RequestService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RequestServiceImpl implements RequestService {
  @Autowired private RequestRepository requestRepository;

  @Override
  public SmsRequest addSmsRequest(SmsRequest smsRequest) throws Exception{
    try {
      return requestRepository.save(smsRequest);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @Override
  public Optional<SmsRequest> getSmsRequestById(Long id) throws Exception{
    try {
      return requestRepository.findById(id);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @Override
  public void updateSmsRequest(SmsRequest smsRequest) throws Exception{
    try {
      requestRepository.save(smsRequest);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @Override
  public Optional<SmsRequest> getSmsRequestByCorrelationId(UUID correlationId) throws Exception{
    try {
      return requestRepository.findByCorrelationID(correlationId);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }
}
