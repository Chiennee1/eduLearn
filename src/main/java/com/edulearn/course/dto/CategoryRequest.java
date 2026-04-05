package com.edulearn.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name is too long")
    private String name;

    @Size(max = 100, message = "Category slug is too long")
    private String slug;

    private Integer parentId;

    @Size(max = 255, message = "Icon URL is too long")
    private String iconUrl;
}

