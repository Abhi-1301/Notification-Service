package com.NotificationService.Controller;

import com.NotificationService.Constants.Constants;
import com.NotificationService.DTO.BlackListRequestDTO;
import com.NotificationService.DTO.SmsRequestDTO;
import com.NotificationService.DTO.SmsSearchRequestDTO;
import com.NotificationService.Exception.InvalidInputException;
import com.NotificationService.Exception.SmsRequestCreationException;
import com.NotificationService.Exception.SmsRequestNotFoundException;
import com.NotificationService.Kafka.KafkaProducer;
import com.NotificationService.Kafka.KafkaTemplateEntity;
import com.NotificationService.Model.BlackListNumber;
import com.NotificationService.Model.Sms;
import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Service.BlackListService;
import com.NotificationService.Service.ElasticSearchService;
import com.NotificationService.Service.RequestService;
import com.NotificationService.Util.GenericResponseEntity;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Constants.ENDPOINT)
public class NotificationController {
  private RequestService requestService;

  private BlackListService blackListService;

  private KafkaProducer kafkaProducer;

  private ElasticSearchService elasticSearchService;

  final String phoneNumberRegex = Constants.PHONE_NUMBER_REGEX;

  @Autowired
  public NotificationController(
      RequestService requestService,
      BlackListService blackListService,
      KafkaProducer kafkaProducer,
      ElasticSearchService elasticSearchService) {
    this.requestService = requestService;
    this.blackListService = blackListService;
    this.kafkaProducer = kafkaProducer;
    this.elasticSearchService = elasticSearchService;
  }

  @GetMapping(Constants.ENDPOINT_FETCH_SMS_BY_ID)
  public ResponseEntity fetchSmsById(@PathVariable Long requestId) {
    try {
      Optional<SmsRequest> searchResult = requestService.getSmsRequestById(requestId);
      if (searchResult.isEmpty()) {
        throw new SmsRequestNotFoundException(Constants.ERROR_SMS_NOT_FOUND + requestId);
      }
      return ResponseEntity.status(HttpStatus.OK)
          .body(GenericResponseEntity.successResponse(searchResult, "SUCCESS"));
    } catch (SmsRequestNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(GenericResponseEntity.errorResponse(null, ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_UNEXPECTED));
    }
  }

  @PostMapping(Constants.ENDPOINT_BLACKLIST)
  public ResponseEntity addBlackList(@RequestBody BlackListRequestDTO blackListRequestDTO) {
    try {
      List<BlackListNumber> phoneNumbersToBlackList = blackListRequestDTO.getPhoneNumbers();
      for (int i = 0; i < phoneNumbersToBlackList.size(); ++i) {
        if (!isValidPhoneNumber(phoneNumbersToBlackList.get(i).getPhoneNumber()))
          throw new InvalidInputException(Constants.ERROR_MISSING_PHONE_NUMBER);
      }
      for (BlackListNumber blackListNumber : phoneNumbersToBlackList) {
        blackListService.addBlackList(blackListNumber);
      }
      return ResponseEntity.status(HttpStatus.OK)
          .body(GenericResponseEntity.successResponse("Successfully blacklisted", "SUCCESS"));
    } catch (InvalidInputException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(GenericResponseEntity.errorResponse(null, ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_UNEXPECTED));
    }
  }

  @GetMapping(Constants.ENDPOINT_BLACKLIST)
  public ResponseEntity fetchBlackList() {
    try {
      List<String> blackListNumbers = new ArrayList<>();
      blackListService
          .fetchAll()
          .forEach(blackListNumber -> blackListNumbers.add(blackListNumber.getPhoneNumber()));
      return ResponseEntity.status(HttpStatus.OK)
          .body(GenericResponseEntity.successResponse(blackListNumbers, "SUCCESS"));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_UNEXPECTED));
    }
  }

  @DeleteMapping(Constants.ENDPOINT_BLACKLIST)
  public ResponseEntity deleteBlackList(@RequestBody BlackListRequestDTO blackListRequestDTO) {
    try {
      List<BlackListNumber> phoneNumbersToBlackList = blackListRequestDTO.getPhoneNumbers();
      for (int i = 0; i < phoneNumbersToBlackList.size(); ++i) {
        if (!isValidPhoneNumber(phoneNumbersToBlackList.get(i).getPhoneNumber()))
          throw new InvalidInputException(Constants.ERROR_MISSING_PHONE_NUMBER);
      }
      for (int i = 0; i < phoneNumbersToBlackList.size(); ++i) {
        blackListService.deleteBlackList(phoneNumbersToBlackList.get(i));
      }
      return ResponseEntity.status(HttpStatus.OK)
          .body(GenericResponseEntity.successResponse("Successfully whitelisted", "SUCCESS"));
    } catch (InvalidInputException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(GenericResponseEntity.errorResponse(null, ex.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_UNEXPECTED));
    }
  }

  @PostMapping(Constants.SEND_SMS)
  public ResponseEntity sendSms(@RequestBody SmsRequestDTO smsRequestDTO) {
    try {
      String phoneNumber = smsRequestDTO.getPhoneNumber();
      String message = smsRequestDTO.getMessage();

      if (!isValidPhoneNumber(phoneNumber)) {
        throw new InvalidInputException(Constants.ERROR_INVALID_PHONE_FORMAT);
      }
      if (blackListService.isBlackListed(new BlackListNumber(phoneNumber))) {
        throw new InvalidInputException(Constants.ERROR_BLACKLISTED_PHONE);
      }

      SmsRequest smsRequest = new SmsRequest(phoneNumber, message);
      SmsRequest savedSmsRequest = requestService.addSmsRequest(smsRequest);

      if (savedSmsRequest == null) {
        throw new SmsRequestCreationException(Constants.ERROR_FAILED_CREATE_SMS_REQUEST);
      }

      kafkaProducer.sendRequestId(new KafkaTemplateEntity(savedSmsRequest.getCorrelationID()));

      return ResponseEntity.status(HttpStatus.OK)
          .body(
              GenericResponseEntity.successResponse(
                  createSuccessResponse(
                      "Successfully Sent", savedSmsRequest.getCorrelationID().toString()),
                  "SUCCESS"));
    } catch (InvalidInputException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(GenericResponseEntity.errorResponse(null, e.getMessage()));
    } catch (SmsRequestCreationException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_UNEXPECTED));
    }
  }

  @GetMapping(Constants.FILTER_BY_PHONE_AND_TIME)
  public ResponseEntity getSmsByPhoneNumberAndTime(
      @RequestBody SmsSearchRequestDTO smsSearchRequestDTO,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      if (!isValidPhoneNumber(smsSearchRequestDTO.getPhoneNumber())) {
        throw new InvalidInputException(Constants.ERROR_INVALID_PHONE_FORMAT);
      }
      List<SmsRequest> searchResults =
          elasticSearchService.getSmsByPhoneNumberAndTime(
              smsSearchRequestDTO.getPhoneNumber(),
              smsSearchRequestDTO.getStartTime(),
              smsSearchRequestDTO.getEndTime(),
              PageRequest.of(page, size));
      return ResponseEntity.status(HttpStatus.OK)
          .body(GenericResponseEntity.successResponse(searchResults, "SUCCESS"));
    } catch (InvalidInputException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(GenericResponseEntity.errorResponse(null, e.getMessage()));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_FETCH_SMS_TIME));
    }
  }

  @GetMapping(Constants.FILTER_BY_TEXT)
  public ResponseEntity getSmsByMessage(
      @RequestBody Sms sms,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    try {
      if (sms.getText() == null || sms.getText().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_EMPTY_TEXT));
      }

      List<SmsRequest> searchResults =
          elasticSearchService.getSmsByText(sms.getText(), PageRequest.of(page, size));
      return ResponseEntity.status(HttpStatus.OK)
          .body(GenericResponseEntity.successResponse(searchResults, "SUCCESS"));
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(GenericResponseEntity.errorResponse(null, Constants.ERROR_FETCH_SMS_MESSAGE));
    }
  }

  private static Map<String, Object> createSuccessResponse(String comments, String requestId) {
    Map<String, Object> data = new HashMap<>();
    data.put("requestId", requestId);
    data.put("comments", comments);
    return data;
  }

  private boolean isValidPhoneNumber(String phoneNumber) {
    return phoneNumber != null && phoneNumber.matches(phoneNumberRegex);
  }
}
