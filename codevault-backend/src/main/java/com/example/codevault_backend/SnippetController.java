package com.example.codevault_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// @RestController tells Spring Boot: "This is the Order Window / Waiter."
// It listens for incoming HTTP requests from users or frontends and sends back JSON responses.
@RestController
@RequestMapping("/api/snippets")
@CrossOrigin(origins = "*")
public class SnippetController {

    // We inject our Head Chef (SnippetService) here using @Autowired
    @Autowired
    private SnippetService snippetService;

    // 1. CREATE A NEW SNIPPET
    // HTTP Method: POST
    // URL: http://localhost:8080/api/snippets
    @PostMapping
    public Snippet createSnippet(@RequestBody Snippet snippet) {
        return snippetService.saveSnippet(snippet);
    }

    // 2. GET ALL OR SEARCH SNIPPETS
    // HTTP Method: GET
    // URL: http://localhost:8080/api/snippets
    // URL with Search: http://localhost:8080/api/snippets?search=sorting
    @GetMapping
    public List<Snippet> getAllSnippets(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return snippetService.searchSnippets(search);
        }
        return snippetService.getAllSnippets();
    }


    // 3. GET A SINGLE SNIPPET BY ID
    // HTTP Method: GET
    // URL: http://localhost:8080/api/snippets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Snippet> getSnippetById(@PathVariable Long id) {
        Optional<Snippet> snippet = snippetService.getSnippetById(id);
        if (snippet.isPresent()) {
            return ResponseEntity.ok(snippet.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. DELETE A SNIPPET BY ID
    // HTTP Method: DELETE
    // URL: http://localhost:8080/api/snippets/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        if (snippetService.deleteSnippet(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
