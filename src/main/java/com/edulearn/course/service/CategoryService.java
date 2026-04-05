package com.edulearn.course.service;

import com.edulearn.common.util.SlugUtils;
import com.edulearn.course.dto.CategoryRequest;
import com.edulearn.course.dto.CategoryResponse;
import com.edulearn.course.entity.Category;
import com.edulearn.course.repository.CategoryRepository;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Integer id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessException("Category name already exists", HttpStatus.CONFLICT);
        }

        String slug = resolveSlug(request.getSlug(), name);
        if (categoryRepository.existsBySlug(slug)) {
            throw new BusinessException("Category slug already exists", HttpStatus.CONFLICT);
        }

        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .parent(resolveParent(request.getParentId(), null))
                .iconUrl(request.getIconUrl())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest request) {
        Category category = getEntity(id);

        String name = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new BusinessException("Category name already exists", HttpStatus.CONFLICT);
        }

        String slug = resolveSlug(request.getSlug(), name);
        if (categoryRepository.existsBySlugAndIdNot(slug, id)) {
            throw new BusinessException("Category slug already exists", HttpStatus.CONFLICT);
        }

        category.setName(name);
        category.setSlug(slug);
        category.setParent(resolveParent(request.getParentId(), id));
        category.setIconUrl(request.getIconUrl());
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Integer id) {
        Category category = getEntity(id);
        categoryRepository.delete(category);
    }

    private Category getEntity(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private Category resolveParent(Integer parentId, Integer currentId) {
        if (parentId == null) {
            return null;
        }
        if (currentId != null && currentId.equals(parentId)) {
            throw new BusinessException("Category cannot be parent of itself", HttpStatus.BAD_REQUEST);
        }
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
    }

    private String resolveSlug(String rawSlug, String name) {
        String base = (rawSlug == null || rawSlug.isBlank()) ? name : rawSlug;
        String slug = SlugUtils.toSlug(base);
        if (slug.isBlank()) {
            throw new BusinessException("Category slug is invalid", HttpStatus.BAD_REQUEST);
        }
        return slug;
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .iconUrl(category.getIconUrl())
                .build();
    }
}

