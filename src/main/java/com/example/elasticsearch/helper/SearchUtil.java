package com.example.elasticsearch.helper;

import com.example.elasticsearch.search.SearchRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchUtil {

    public static SearchRequest buildSearchRequest(String indexName, SearchRequestDto searchRequestDto) {
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(getQueryBuilder(searchRequestDto));

            if (searchRequestDto.getSortBy() != null)
                builder = builder.sort(
                        searchRequestDto.getSortBy(),
                        searchRequestDto.getSortOrder() != null ? searchRequestDto.getSortOrder() : SortOrder.ASC
                );

            SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static QueryBuilder getQueryBuilder(SearchRequestDto searchRequestDto) {
        if (searchRequestDto == null)
            return null;

        List<String> fields = searchRequestDto.getFields();
        if (CollectionUtils.isEmpty(fields))
            return null;

        if (fields.size() > 1) {
            MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(searchRequestDto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .operator(Operator.AND);

            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }

        return QueryBuilders.matchQuery(fields.get(0), searchRequestDto.getSearchTerm()).operator(Operator.AND);
    }
}
