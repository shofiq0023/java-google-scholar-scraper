package com.shofiqul.scraper.servicesImpl;

import com.shofiqul.scraper.dtos.ScraperReqDto;
import com.shofiqul.scraper.services.ScraperService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScraperServiceImpl implements ScraperService {
    Logger logger = LoggerFactory.getLogger(ScraperServiceImpl.class);

    @Override
    public ResponseEntity<Boolean> scrapeFromGoogleScholar(ScraperReqDto reqDto) {
        try {
            return new ResponseEntity<>(true,  HttpStatus.OK);
        } catch (Exception e) {
            logger.error("ScraperService Exception: ", e);
            return new ResponseEntity<>(false,  HttpStatus.OK);
        }
    }
}
