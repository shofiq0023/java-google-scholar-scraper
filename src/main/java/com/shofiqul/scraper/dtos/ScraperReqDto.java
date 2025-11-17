package com.shofiqul.scraper.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScraperReqDto {
    private List<String> keywords;
    private String yearSince;
    private int page;
}
