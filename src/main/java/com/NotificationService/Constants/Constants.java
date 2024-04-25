package com.NotificationService.Constants;

public final class Constants {
  public static final String AUTHENTICATION_API_KEY = "";
  public static final String ENDPOINT = "/v1";
  public static final String PHONE_NUMBER_REGEX = "^\\+[0-9]{12}$";
  public static final String ENDPOINT_FETCH_SMS_BY_ID = "/sms/{requestId}";
  public static final String ENDPOINT_BLACKLIST = "/blacklist";
  public static final String SEND_SMS = "/sms/send";
  public static final String FILTER_BY_PHONE_AND_TIME = "/sms/filterByPhoneAndTime";
  public static final String FILTER_BY_TEXT = "/sms/filterByText";
  public static final String ERROR_SMS_NOT_FOUND = "SMS request not found with ID: ";
  public static final String ERROR_MISSING_PHONE_NUMBER =
      "Phone Number is Mandatory, in right format";
  public static final String ERROR_FAILED_CREATE_SMS_REQUEST = "Failed to create SMS request";
  public static final String ERROR_INVALID_PHONE_FORMAT = "Invalid phone number format";
  public static final String ERROR_BLACKLISTED_PHONE = "Given Phone Number is Blacklisted";
  public static final String ERROR_EMPTY_TEXT = "Text cannot be empty";
  public static final String ERROR_FETCH_SMS_TIME =
      "An error occurred while fetching SMS requests by phone number and time";
  public static final String ERROR_FETCH_SMS_MESSAGE =
      "An error occurred while fetching SMS requests by message";
  public static final String ERROR_UNEXPECTED = "An unexpected error occurred";
  public static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
  public static final String KAFKA_TOPIC_NOTIFICATION_SEND_SMS = "notification.send_sms";
  public static final String KAFKA_GROUP_ID = "group-id";
  public static final String API_ENDPOINT = "https://api.imiconnect.in/resources/v1/messaging";
  public static final String THIRD_PARTY_API_KEY = "";
}
