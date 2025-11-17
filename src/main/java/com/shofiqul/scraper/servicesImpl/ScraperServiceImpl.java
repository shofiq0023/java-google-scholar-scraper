package com.shofiqul.scraper.servicesImpl;

import com.shofiqul.scraper.dtos.ScraperReqDto;
import com.shofiqul.scraper.services.ScraperService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {
    Logger logger = LoggerFactory.getLogger(ScraperServiceImpl.class);

    @Value("${google.scholar.website}")
    private String googleScholarWebsite;

    @Value("${google.scholar.year.filter.name}")
    private String yearFilterName;

    @Value("${google.scholar.search.query.name}")
    private String searchQueryName;

    @Value("${google.scholar.search.pagination.name}")
    private String paginationName;

    @Override
    public ResponseEntity<Boolean> scrapeFromGoogleScholar(ScraperReqDto reqDto) {
        try {
            reqDto.getKeywords().forEach(keyword -> {
                try {
                    getDocumentFromHtml(reqDto.getYearSince(), keyword);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return new ResponseEntity<>(true,  HttpStatus.OK);
        } catch (Exception e) {
            logger.error("ScraperService Exception: ", e);
            return new ResponseEntity<>(false,  HttpStatus.OK);
        }
    }

    private Document getDocumentFromHtml(String yearSince, String query) throws IOException {
        String url = googleScholarWebsite + "?" + yearFilterName + "=" + yearSince + "&" + searchQueryName + "=" + query;
        Document doc  = Jsoup.connect(url).get();
        return doc;
    }
}
