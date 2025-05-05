package com.trueque_api.staff.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListingRequestDTO {
    private String title;
    private String description;
    private String category;
    private String condition;
    private String city;
    private String state;
    private List<String> images;
}