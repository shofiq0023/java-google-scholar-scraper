package com.shofiqul.scraper.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PapersInformationDto {
    private String title;
    private List<PaperAuthors> authors;
    private String description;
    private String citedBy;
}
