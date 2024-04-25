package com.NotificationService.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Service.Impl.ElasticSearchServiceImpl;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

@ExtendWith(MockitoExtension.class)
public class ElasticSearchServiceTests {
  @Mock private ElasticsearchOperations elasticsearchOperations;
  @InjectMocks private ElasticSearchServiceImpl elasticSearchService;

  @BeforeEach
  void setUp() {
    reset(elasticsearchOperations);
  }

  @Test
  public void saveSmsRequestTest() throws Exception {
    SmsRequest smsRequest = new SmsRequest();
    elasticSearchService.saveSmsRequest(smsRequest);
    verify(elasticsearchOperations, times(1)).save(smsRequest);
  }

  @Test
  public void getSmsByPhoneNumberAndTimeTest() throws Exception {
    String phoneNumber = "+911234567890";
    Date startTime = new Date();
    Date endTime = new Date();
    Pageable pageable = mock(Pageable.class);
    SearchHits<SmsRequest> searchHits = mock(SearchHits.class);

    List<SearchHit<SmsRequest>> searchHitList = Collections.emptyList();
    when(searchHits.getSearchHits()).thenReturn(searchHitList);
    when(elasticsearchOperations.search(any(Query.class), eq(SmsRequest.class)))
        .thenReturn(searchHits);

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    elasticSearchService.getSmsByPhoneNumberAndTime(phoneNumber, startTime, endTime, pageable);
    verify(elasticsearchOperations, times(1)).search(queryCaptor.capture(), eq(SmsRequest.class));

    Query captured = queryCaptor.getValue();
    Criteria criteria =
        new Criteria("phoneNumber")
            .is(phoneNumber)
            .and(new Criteria("createdAt").between(startTime, endTime));
    Query expected = new CriteriaQuery(criteria);
    expected.setPageable(pageable);
    assertEquals(
        ((CriteriaQuery) expected).getCriteria().toString(),
        ((CriteriaQuery) captured).getCriteria().toString());
  }

  @Test
  public void getSmsByTextTest() throws Exception {
    String text = "text";
    Pageable pageable = mock(Pageable.class);
    SearchHits<SmsRequest> searchHits = mock(SearchHits.class);

    List<SearchHit<SmsRequest>> searchHitList = Collections.emptyList();
    when(searchHits.getSearchHits()).thenReturn(searchHitList);
    when(elasticsearchOperations.search(any(Query.class), eq(SmsRequest.class)))
        .thenReturn(searchHits);

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    elasticSearchService.getSmsByText(text, pageable);
    verify(elasticsearchOperations, times(1)).search(queryCaptor.capture(), eq(SmsRequest.class));

    Query captured = queryCaptor.getValue();
    Criteria criteria = new Criteria("message").expression("*" + text + "*");
    Query expected = new CriteriaQuery(criteria);
    expected.setPageable(pageable);
    assertEquals(
        ((CriteriaQuery) expected).getCriteria().toString(),
        ((CriteriaQuery) captured).getCriteria().toString());
  }
}
