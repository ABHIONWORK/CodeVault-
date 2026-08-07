package com.example.codevault_backend;

// We are importing tools that let us talk to the database
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// @Entity tells the Kitchen: "Hey, this is a blueprint for a table in our database Pantry!"
@Entity
public class Snippet {

    // @Id means this is the unique barcode for every snippet. No two snippets have
    // the same ID.
    @Id
    // @GeneratedValue tells the database to automatically count up the IDs for us
    // (1, 2, 3...)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A space to store the title of the code snippet (like "How to sort an array")
    private String title;

    // A space to store the actual code itself
    private String codeContent;

    // A space to store what language the code is in (like "Java" or "Python")
    private String language;

    // A space to store comma-separated tags (like "sorting, arrays, algorithmic")
    private String tags;

    // This is an empty "constructor". It is like a blank order ticket the kitchen
    // needs to start working.
    public Snippet() {
    }

    // Below are the "Getters" and "Setters".
    // They are little windows that let the rest of our app look at or change the
    // data inside this file.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}