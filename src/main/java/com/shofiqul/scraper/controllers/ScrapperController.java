package com.shofiqul.scraper.controllers;

import com.shofiqul.scraper.dtos.PapersInformationDto;
import com.shofiqul.scraper.dtos.ScraperReqDto;
import com.shofiqul.scraper.services.ScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraper")
@RequiredArgsConstructor
public class ScrapperController {
    private final ScraperService scraperService;

    @PostMapping("/scrape")
    public ResponseEntity<List<PapersInformationDto>> scrapeFromGoogleScholar(@RequestBody ScraperReqDto reqDto) {
        return scraperService.scrapeFromGoogleScholar(reqDto);
    }
}
