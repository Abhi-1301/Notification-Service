package com.NotificationService.ElasticSearch;

import com.NotificationService.Model.SmsRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticSearchRepository extends ElasticsearchRepository<SmsRequest,Long> {
}
