package com.example.elasticsearch.helper;

import com.example.elasticsearch.search.SearchRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchUtil {

    public static SearchRequest buildSearchRequest(String indexName, SearchRequestDto searchRequestDto) {
        try {
            int page = searchRequestDto.getPage();
            int size = searchRequestDto.getSize();
            int from = page <= 0 ? 0 : page * size;

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .size(size)
                    .from(from)
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

    public static SearchRequest buildSearchRequest(String indexName, String field, Date date) {
        try {
            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(getRangeQueryBuilder(field, date));

            SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(String indexName, SearchRequestDto searchRequestDto, Date date) {
        try {
            QueryBuilder searchQuery = getQueryBuilder(searchRequestDto);
            QueryBuilder dateQuery = getRangeQueryBuilder("created", date);

            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(searchQuery).must(dateQuery);

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(boolQuery);

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


    private static QueryBuilder getRangeQueryBuilder(String field, Date date) {
        return QueryBuilders.rangeQuery(field).gte(date);
    }
}
