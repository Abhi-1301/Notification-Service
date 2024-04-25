package com.NotificationService.Configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Slf4j
@EnableElasticsearchRepositories(basePackages = "com.NotificationService.ElasticSearch")
@EnableJpaRepositories(basePackages = "com.NotificationService.Repository")
public class ElasticSearchConfig extends ElasticsearchConfiguration {
  @Override
  public ClientConfiguration clientConfiguration() {
    ClientConfiguration configuration =
        ClientConfiguration.builder().connectedTo("elasticsearch:9200").build();
    return configuration;
  }
}
