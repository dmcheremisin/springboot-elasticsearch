package com.example.elasticsearch.service;

import com.example.elasticsearch.helper.Indices;
import com.example.elasticsearch.helper.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexService {
    private final List<String> INDICES_TO_CREATE = List.of(Indices.VEHICLE_INDEX);

    private final RestHighLevelClient client;

    @PostConstruct
    public void loadIndices() {
        createIndices(false);
    }

    public void createIndices(boolean recreateIndices) {
        String settings = Util.loadAsString("elasticsearch/settings.json");
        if (settings == null)
            return;

        for (String indexName : INDICES_TO_CREATE) {
            try {
                boolean indexExists = client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
                if (indexExists) {
                    log.info("Index exists: {}, recreate: {}", indexName, recreateIndices);
                    if (!recreateIndices)
                        continue;

                    client.indices().delete(new DeleteIndexRequest(indexName), RequestOptions.DEFAULT);
                }

                String mappings = Util.loadAsString("elasticsearch/mappings/" + indexName + ".json");
                if (mappings == null) {
                    log.error("Mapping is not found for index name: {}", indexName);
                    continue;
                }

                log.info("Create index for name: {}", indexName);
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                request.settings(settings, XContentType.JSON);
                request.mapping(mappings, XContentType.JSON);

                client.indices().create(request, RequestOptions.DEFAULT);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
