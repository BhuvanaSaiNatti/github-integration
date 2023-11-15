package com.redcarepharmacy.controller;

import com.redcarepharmacy.model.Language;
import com.redcarepharmacy.model.Repository;
import com.redcarepharmacy.model.response.RepositoriesResponse;
import com.redcarepharmacy.service.GithubService;
import com.redcarepharmacy.validator.ValidDate;
import com.redcarepharmacy.validator.ValidLanguage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class RepositoriesController {
    private static final Logger logger = LoggerFactory.getLogger(RepositoriesController.class);

    @Autowired
    private final GithubService githubService;

    public RepositoriesController(GithubService githubService) {
        this.githubService = githubService;
    }

    /**
     * Fetches most popular results sorted by stars from Github
     * @param language Filters on language if provided
     * @param createdAfter Fetches repos created on or after this date
     * @param limitCount Fetches this number of results
     * @param page Fetches the results on this page
     * @return Returns the matched list of github repositories
     */
    @RequestMapping(value = "/repositories", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RepositoriesResponse> repositories(
            @RequestParam(required = false) @ValidLanguage(enumClass = Language.class) @Valid String language,
            @RequestParam(required = false) @ValidDate String createdAfter,
            @RequestParam(defaultValue = "30") @Min(1) @Max(100) int limitCount,
            @RequestParam(defaultValue = "1") @Min(1) int page) {
        logger.info(
                "Received request to fetch repos sorted by stars - language {}, createdAfter {}, limitCount {}, page {}",
                language,
                createdAfter,
                limitCount,
                page);
        List<Repository> repositoryList =
                githubService.fetchReposSortedByStars(language, createdAfter, limitCount, page);
        RepositoriesResponse repositoriesResponse = RepositoriesResponse.builder()
                .total_count(repositoryList.size())
                .items(repositoryList)
                .build();
        return ResponseEntity.ok().body(repositoriesResponse);
    }
}
