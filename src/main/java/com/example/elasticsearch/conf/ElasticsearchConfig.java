package com.example.elasticsearch.conf;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    public String elasticsearchHost;

    @Value("${elasticsearch.port}")
    public Integer elasticsearchPort;

    @Override
    public RestHighLevelClient elasticsearchClient() {

        RestClient restClient = RestClient.builder(
                new HttpHost(elasticsearchHost, elasticsearchPort)).build();

        return new RestHighLevelClientBuilder(restClient)
                .setApiCompatibilityMode(true)
                .build();
    }


}
