package com.shofiqul.scraper.services;

import com.shofiqul.scraper.dtos.PapersInformationDto;
import com.shofiqul.scraper.dtos.ScraperReqDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ScraperService {
    ResponseEntity<List<PapersInformationDto>> scrapeFromGoogleScholar(ScraperReqDto reqDto);
}
