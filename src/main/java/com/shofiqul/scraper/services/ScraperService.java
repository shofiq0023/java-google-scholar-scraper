package com.shofiqul.scraper.services;

import com.shofiqul.scraper.dtos.ScraperReqDto;
import org.springframework.http.ResponseEntity;

public interface ScraperService {
    ResponseEntity<Boolean> scrapeFromGoogleScholar(ScraperReqDto reqDto);
}
