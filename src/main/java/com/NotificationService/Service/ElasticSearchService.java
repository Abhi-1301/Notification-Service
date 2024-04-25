package com.NotificationService.Service;

import com.NotificationService.Model.SmsRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface ElasticSearchService {
	void saveSmsRequest (SmsRequest smsRequest) throws Exception;
	List<SmsRequest> getSmsByPhoneNumberAndTime(String phoneNumber, Date startTime, Date endTime, Pageable pageable) throws Exception;
	List<SmsRequest> getSmsByText(String text, Pageable pageable) throws Exception;
}
