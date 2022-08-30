package com.example.elasticsearch.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchRequestDto extends PagedRequestDto {

    private String searchTerm;

    private List<String> fields;

    private String sortBy;

    private SortOrder sortOrder;

}
