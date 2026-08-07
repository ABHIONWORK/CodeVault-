package com.example.codevault_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service tells Spring Boot: "This is our Head Chef!"
// The Chef contains all the recipe business logic. The Waiter (Controller) takes orders from customers
// and hands them over to the Chef (Service), who works with the Pantry Manager (Repository) to get the job done.
@Service
public class SnippetService {

    // @Autowired tells Spring Boot: "Hey Robot Assistant, automatically plug in the SnippetRepository here!"
    // You don't need to write 'new SnippetRepository()'. Spring Boot finds it and connects it for us.
    @Autowired
    private SnippetRepository snippetRepository;

    // Method to save a new snippet to the pantry
    public Snippet saveSnippet(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    // Method to get all snippets from the pantry
    public List<Snippet> getAllSnippets() {
        return snippetRepository.findAll();
    }

    // Method to get a single snippet by ID
    public Optional<Snippet> getSnippetById(Long id) {
        return snippetRepository.findById(id);
    }

    // Method to search snippets by keyword matching title or tags
    public List<Snippet> searchSnippets(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return snippetRepository.findAll();
        }
        return snippetRepository.findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(keyword, keyword);
    }

    // Traditional indexing loop filtering to process/verify snippets (No enhanced for loops used!)
    public List<Snippet> filterSnippetsWithTraditionalLoop(List<Snippet> allSnippets, String keyword) {
        List<Snippet> matchingSnippets = new java.util.ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();

        for (int i = 0; i < allSnippets.size(); i++) {
            Snippet current = allSnippets.get(i);
            boolean titleMatches = current.getTitle() != null && current.getTitle().toLowerCase().contains(lowerKeyword);
            boolean tagsMatch = current.getTags() != null && current.getTags().toLowerCase().contains(lowerKeyword);

            if (titleMatches || tagsMatch) {
                matchingSnippets.add(current);
            }
        }
        return matchingSnippets;
    }

    // Method to delete a snippet by ID
    public boolean deleteSnippet(Long id) {
        if (snippetRepository.existsById(id)) {
            snippetRepository.deleteById(id);
            return true;
        }
        return false;
    }
}

