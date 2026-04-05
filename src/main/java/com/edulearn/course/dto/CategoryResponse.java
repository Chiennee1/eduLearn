package com.edulearn.course.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private final Integer id;
    private final String name;
    private final String slug;
    private final Integer parentId;
    private final String iconUrl;
}

