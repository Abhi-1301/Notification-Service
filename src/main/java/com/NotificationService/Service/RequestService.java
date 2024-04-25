package com.NotificationService.Service;

import com.NotificationService.Model.SmsRequest;
import java.util.Optional;
import java.util.UUID;

public interface RequestService {
	SmsRequest addSmsRequest(SmsRequest smsRequest) throws Exception;
	Optional<SmsRequest> getSmsRequestById(Long id) throws Exception;
	void updateSmsRequest(SmsRequest smsRequest) throws Exception;
	Optional<SmsRequest> getSmsRequestByCorrelationId(UUID correlationId) throws Exception;
}