package com.example.elasticsearch.service;

import com.example.elasticsearch.document.Vehicle;
import com.example.elasticsearch.helper.Indices;
import com.example.elasticsearch.helper.SearchUtil;
import com.example.elasticsearch.search.SearchRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RestHighLevelClient client;

    public Boolean index(Vehicle vehicle) {
        try {
            final String vehicleAsString = MAPPER.writeValueAsString(vehicle);

            IndexRequest indexRequest = new IndexRequest(Indices.VEHICLE_INDEX);
            indexRequest.id(vehicle.getId());
            indexRequest.source(vehicleAsString, XContentType.JSON);

            IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
            return response != null && response.status().equals(RestStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public Vehicle getById(String vehicleId) {
        try {
            GetResponse response = client.get(new GetRequest(Indices.VEHICLE_INDEX, vehicleId), RequestOptions.DEFAULT);
            if (response == null || response.getSource() == null)
                return null;

            return MAPPER.readValue(response.getSourceAsString(), Vehicle.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public List<Vehicle> search(SearchRequestDto searchRequestDto) {
        SearchRequest searchRequest = SearchUtil.buildSearchRequest(Indices.VEHICLE_INDEX, searchRequestDto);
        if(searchRequest == null) {
            log.error("Failed to build search request for: {}", searchRequestDto);
            return Collections.emptyList();
        }
        return executeSearchRequest(searchRequest);
    }


    public List<Vehicle> getAllVehiclesCreatedSince(Date date) {
        SearchRequest searchRequest = SearchUtil.buildSearchRequest(Indices.VEHICLE_INDEX, "created", date);
        return executeSearchRequest(searchRequest);
    }

    private List<Vehicle> executeSearchRequest(SearchRequest searchRequest) {
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] hits = response.getHits().getHits();
            List<Vehicle> vehicles = new ArrayList<>();
            for (SearchHit hit : hits)
                vehicles.add(MAPPER.readValue(hit.getSourceAsString(), Vehicle.class));

            return vehicles;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<Vehicle> searchCreatedSinceWithRequest(SearchRequestDto searchRequestDto, Date date) {
        SearchRequest request = SearchUtil.buildSearchRequest(Indices.VEHICLE_INDEX, searchRequestDto, date);

        return executeSearchRequest(request);
    }
}
