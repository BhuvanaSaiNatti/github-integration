package com.redcarepharmacy.model.response;

import com.redcarepharmacy.model.Repository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositoriesResponse {
    int total_count;
    private List<Repository> items;
}
