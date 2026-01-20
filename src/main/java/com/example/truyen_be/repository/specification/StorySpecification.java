package com.example.truyen_be.repository.specification;

import com.example.truyen_be.entity.Category;
import com.example.truyen_be.entity.Story;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StorySpecification {

    public static Specification<Story> filterStories(
            List<String> includeCategorySlugs,
            List<String> excludeCategorySlugs,
            Integer minChapters,
            String status,
            String sortField) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc bao gồm thể loại (Phải có TẤT CẢ các thể loại này)
            if (includeCategorySlugs != null && !includeCategorySlugs.isEmpty()) {
                for (String slug : includeCategorySlugs) {
                    // Tạo một join mới cho mỗi thể loại để đảm bảo truyện có đủ tất cả (AND logic)
                    Join<Story, Category> categoryJoin = root.join("categories");
                    predicates.add(cb.equal(categoryJoin.get("slug"), slug));
                }
            }

            // 2. Loại trừ thể loại (Không được chứa bất kỳ thể loại nào trong danh sách này)
            if (excludeCategorySlugs != null && !excludeCategorySlugs.isEmpty()) {
                for (String slug : excludeCategorySlugs) {
                    // Sử dụng Subquery để tìm những truyện có category bị loại trừ
                    Subquery<Long> subquery = query.subquery(Long.class);
                    var subRoot = subquery.from(Story.class);
                    Join<Story, Category> subCategoryJoin = subRoot.join("categories");
                    subquery.select(subRoot.get("id"))
                            .where(cb.equal(subCategoryJoin.get("slug"), slug));

                    // Thêm điều kiện: ID của truyện không được nằm trong danh sách subquery
                    predicates.add(cb.not(root.get("id").in(subquery)));
                }
            }

            // 3. Lọc theo số chương (ví dụ: > 0, > 50, > 100)
            if (minChapters != null && minChapters > 0) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("chaptersCount"), minChapters));
            }

            // 4. Lọc theo tình trạng (Đang ra, Hoàn thành)
            if (status != null && !status.equalsIgnoreCase("Tất cả")) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Tránh lặp lại kết quả (Distinct) vì chúng ta có Join
            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}