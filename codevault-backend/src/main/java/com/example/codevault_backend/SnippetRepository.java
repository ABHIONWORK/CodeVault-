package com.example.codevault_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository tells Spring Boot: "This is our Pantry Manager Assistant."
// It handles all direct communication with the MySQL database.
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {

    // Spring Data JPA automatically generates SQL from this method name:
    // SELECT * FROM snippet WHERE LOWER(title) LIKE %keyword% OR LOWER(tags) LIKE %keyword%
    List<Snippet> findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(String titleKey, String tagsKey);
}

