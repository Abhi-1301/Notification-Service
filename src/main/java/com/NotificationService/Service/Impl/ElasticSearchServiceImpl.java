package com.NotificationService.Service.Impl;

import com.NotificationService.Model.SmsRequest;
import com.NotificationService.Service.ElasticSearchService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
  @Autowired private ElasticsearchOperations elasticsearchOperations;

  @Override
  public void saveSmsRequest(SmsRequest smsRequest) throws Exception{
    try {
      elasticsearchOperations.save(smsRequest);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @Override
  public List<SmsRequest> getSmsByPhoneNumberAndTime(
      String phoneNumber, Date startTime, Date endTime, Pageable pageable) throws Exception{
    try {
      Criteria criteria =
          new Criteria("phoneNumber")
              .is(phoneNumber)
              .and(new Criteria("createdAt").between(startTime, endTime));
      Query query = new CriteriaQuery(criteria);
      query.setPageable(pageable);
      SearchHits<SmsRequest> searchHits = elasticsearchOperations.search(query, SmsRequest.class);
      return searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @Override
  public List<SmsRequest> getSmsByText(String text, Pageable pageable) throws Exception{
    try {
      Criteria criteria = new Criteria("message").expression("*" + text + "*");
      Query query = new CriteriaQuery(criteria);
      query.setPageable(pageable);
      SearchHits<SmsRequest> searchHits = elasticsearchOperations.search(query, SmsRequest.class);
      return searchHits.getSearchHits().stream().map(SearchHit::getContent).toList();
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }
}
