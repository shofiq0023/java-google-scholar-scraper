package com.shofiqul.scraper.servicesImpl;

import com.shofiqul.scraper.dtos.PaperAuthors;
import com.shofiqul.scraper.dtos.PapersInformationDto;
import com.shofiqul.scraper.dtos.ScraperReqDto;
import com.shofiqul.scraper.services.ScraperService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Value("${scraping.pause.in.ms}")
    private String scrapingPauseInMs;

    @Value("${scraping.result.per.page}")
    private String scrapingResultPerPage;

    @Override
    public ResponseEntity<List<PapersInformationDto>> scrapeFromGoogleScholar(ScraperReqDto reqDto) {
        try {
            List<PapersInformationDto> papersInformationDtoList = new ArrayList<>();
            for (String keyword : reqDto.getKeywords()) {
                try {
                    // Calculate the request pagination
                    // For 2 page, the result will be (2 - 1) * 10 = 10 pagination
                    // So the pagination will start at 0 and end at 10
                    int pageSize = reqDto.getPage();
                    int requestPage = Integer.parseInt(scrapingResultPerPage);

                    for (int i = 0; i < pageSize; i++) {
                        int currentPage = i * requestPage;
                        papersInformationDtoList.addAll(getDocumentFromHtml(reqDto.getYearSince(), keyword, currentPage));
                        Thread.sleep(Integer.parseInt(scrapingPauseInMs)); // Pausing scraping to prevent anti-scraping
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new ResponseEntity<>(papersInformationDtoList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("ScraperService Exception: ", e);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    private List<PapersInformationDto> getDocumentFromHtml(String yearSince, String query, int currentPage) throws IOException {
        List<PapersInformationDto> response = new ArrayList<>();
        String url = googleScholarWebsite + "?" + paginationName + "=" + currentPage + "&" + yearFilterName + "=" + yearSince + "&" + searchQueryName + "=" + query;

        Document doc = Jsoup.connect(url).get();
        Element mainBodyTag = doc.getElementById("gs_bdy");
        if (mainBodyTag == null) return response;

        Element papersParentDiv = mainBodyTag.getElementById("gs_bdy_ccl");
        if (papersParentDiv == null) return response;

        Element papersMiddlePartHolder = mainBodyTag.getElementById("gs_res_ccl");
        if (papersMiddlePartHolder == null) return response;

        Element papersDivList = mainBodyTag.getElementById("gs_res_ccl_mid");
        if (papersDivList == null) return response;

        Elements papers = papersDivList.getElementsByClass("gs_r gs_or gs_scl");
        response = extractPapersInfo(papers);

        return response;
    }

    private List<PapersInformationDto> extractPapersInfo(Elements papers) {
        List<PapersInformationDto> papersList = new ArrayList<>();

        for (Element paper : papers) {
            Element papersInfoMainDiv = paper.getElementsByClass("gs_ri").first();
            if (papersInfoMainDiv == null) continue;

            PapersInformationDto paperInfo = new PapersInformationDto();
            paperInfo.setTitle(getPaperTitle(papersInfoMainDiv));
            paperInfo.setAuthors(getAuthors(papersInfoMainDiv));
            paperInfo.setDescription(getPaperDescription(papersInfoMainDiv));
            paperInfo.setCitedBy(getCitationNumber(papersInfoMainDiv));

            papersList.add(paperInfo);
        }

        return papersList;
    }

    private String getPaperTitle(Element papersInfoMainDiv) {
        Element paperTitleDiv = papersInfoMainDiv.getElementsByClass("gs_rt").first();
        if (paperTitleDiv == null) return null;

        Element titleAnchorTag = paperTitleDiv.getElementsByTag("a").first();
        if (titleAnchorTag == null) return null;

        return titleAnchorTag.text();
    }

    private List<PaperAuthors> getAuthors(Element papersInfoMainDiv) {
        List<PaperAuthors> paperAuthors = new ArrayList<>();
        Element authorDiv = papersInfoMainDiv.getElementsByClass("gs_a").first();
        if (authorDiv == null) return paperAuthors;

        Elements authors = authorDiv.getElementsByTag("a");
        for (Element author : authors) {
            PaperAuthors paperAuthor = new PaperAuthors();
            paperAuthor.setAuthorName(author.text());
            paperAuthor.setAuthorCitation(googleScholarWebsite + author.attr("href"));

            paperAuthors.add(paperAuthor);
        }

        return paperAuthors;
    }

    private String getPaperDescription(Element papersInfoMainDiv) {
        Element descriptionDiv = papersInfoMainDiv.getElementsByClass("gs_rs").first();
        if (descriptionDiv == null) return null;

        return descriptionDiv.text();
    }

    private String getCitationNumber(Element papersInfoMainDiv) {
        Element papersInfoDivFooter = papersInfoMainDiv.getElementsByClass("gs_fl gs_flb").first();
        if (papersInfoDivFooter == null) return null;

        Elements anchorTags = papersInfoDivFooter.getElementsByTag("a");
        for (Element anchor : anchorTags) {
            if (anchor.text().contains("Cited by")) {
                return anchor.text();
            }
        }

        return null;
    }
}
