package com.example.elasticsearch.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Vehicle {

    private String id;

    private String number;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date created;
}
