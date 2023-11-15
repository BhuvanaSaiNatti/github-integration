package com.redcarepharmacy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Repository {
    private int id;
    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("stargazers_count")
    private Integer stargazersCount;

    private String language;

    @JsonProperty("created_at")
    private Date createdAt;

    private User owner;

    @JsonProperty("html_url")
    private String htmlUrl;

    private String description;
    private String url;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty("pushed_at")
    private Date pushedAt;

    private int score;

    @JsonProperty("git_url")
    private String gitUrl;
}
