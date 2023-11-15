package com.redcarepharmacy.service;

import com.redcarepharmacy.exception.ApplicationException;
import com.redcarepharmacy.exception.GithubException;
import com.redcarepharmacy.model.Language;
import com.redcarepharmacy.model.Repository;
import com.redcarepharmacy.model.response.RepositoriesResponse;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@PropertySource(value = {"application.properties"})
public class GithubService {
    Logger logger = LoggerFactory.getLogger(GithubService.class);

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    CacheManager cacheManager;

    @Value("${github.service.url}")
    private String serviceUrl;

    @Value("${github.service.token:#{null}}")
    private String serviceToken;

    public GithubService(final RestTemplate restTemplate, final CacheManager cacheManager) {
        this.restTemplate = restTemplate;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "${cron.expression}")
    public void evictCache() {
        logger.debug("Evicting cache at: " + LocalDateTime.now());
        Objects.requireNonNull(cacheManager.getCache("repos")).clear();
    }

    /**
     * Fetches most popular results sorted by stars from Github
     * @param language Filters on language if provided
     * @param afterCreatedDate Fetches repos created on or after this date
     * @param limitCount Fetches this number of results
     * @param pageNumber Fetches the results on this page
     * @return Returns the matched list of github repositories
     */
    @Cacheable(value = "repos")
    public List<Repository> fetchReposSortedByStars(
            String language, String afterCreatedDate, int limitCount, int pageNumber) {
        String queryString = buildQueryString(afterCreatedDate, language);
        if (queryString.isBlank()) {
            queryString = "Q&";
        }

        try {
            return executeGithubCall(queryString, limitCount, pageNumber);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatusCode.valueOf(403))
                throw new GithubException(e.getStatusCode().value(), e.getStatusText());
            else throw new GithubException(e.getStatusCode().value(), e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    /**
     * Encodes the language string. Need for languages with special characters like C++ and C#
     * @param language The language
     * @return Encoded string
     */
    private String encodeLanguage(String language) {
        if (language != null) {
            try {
                language = URLEncoder.encode(
                        Language.valueOf(language.trim().toUpperCase()).getName(), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
            }
        }
        return language;
    }

    private String buildQueryString(String date, String language) {
        StringBuilder builder = new StringBuilder();
        language = encodeLanguage(language);
        if (date != null && language != null) {
            builder.append("created:%3E%3D").append(date.trim()).append("+");
            builder.append("language:").append(language).append("&");
        } else if (language != null) {
            builder.append("language:").append(language).append("&");
        } else if (date != null) {
            builder.append("created:%3E%3D").append(date.trim()).append("&");
        }
        return builder.toString();
    }

    /**
     * Calls the github api to fetch the most starred repositories
     * @param query The query string
     * @param limitCount Fetches this number of results
     * @param pageNumber Fetches the results from this page
     * @return Returns the Github repositories that matched the filters
     */
    private List<Repository> executeGithubCall(String query, int limitCount, int pageNumber) {

        HttpEntity<String> requestEntity = new HttpEntity<>(buildHeaders());

        StringBuilder queryWithParams = new StringBuilder(query);
        queryWithParams
                .append("sort=stars&order=desc")
                .append("&per_page=")
                .append(limitCount)
                .append("&page=")
                .append(pageNumber);

        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromUriString(serviceUrl + "/search/repositories?q=" + queryWithParams);
        URI uri = uriComponentsBuilder.build(true).toUri();

        ResponseEntity<RepositoriesResponse> responseEntity =
                restTemplate.exchange(uri, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<>() {});

        return responseEntity.getBody() != null ? responseEntity.getBody().getItems() : new ArrayList<>();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/vnd.github+json");
        if (serviceToken != null && !serviceToken.isBlank()) {
            headers.add("Authorization", "token " + serviceToken);
        }
        return headers;
    }
}
