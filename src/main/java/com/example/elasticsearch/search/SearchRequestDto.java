package com.example.elasticsearch.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchRequestDto {

    private List<String> fields;

    private String searchTerm;

}
