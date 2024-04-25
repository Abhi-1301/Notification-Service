package com.NotificationService.Service;


import java.util.UUID;

public interface SendSmsService {
	void consumeRequestId(UUID correlationId) throws Exception;
}
