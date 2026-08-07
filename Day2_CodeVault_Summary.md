# Day 2: Meet the Chef & Pantry Manager (Repository, Service & Controller)

Welcome to Day 2 of building CodeVault!

In Day 1, we set up our **Kitchen (Spring Boot)**, connected it to the **Pantry (MySQL Database)**, and created our ingredient blueprint (`Snippet.java`).

Today in Day 2, we hired our complete restaurant team:
1. **The Pantry Manager (`SnippetRepository`)**: Opens the database pantry doors and runs queries without writing manual SQL.
2. **The Head Chef (`SnippetService`)**: Holds the secret recipes (business logic) and prepares the dishes.
3. **The Waiter (`SnippetController`)**: Takes customer orders from the web window and hands back the cooked responses in JSON format.

---

## 1. The Pantry Manager: `SnippetRepository.java`

Instead of writing complex SQL commands like `SELECT * FROM snippet`, we use **Spring Data JPA**. We create an **Interface** extending `JpaRepository`.

**File:** `src/main/java/com/example/codevault_backend/SnippetRepository.java`

```java
package com.example.codevault_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// @Repository tells Spring Boot: "This is our Pantry Manager Assistant."
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {
    // JpaRepository gives us out-of-the-box methods:
    // - save(snippet)    -> Save or update a snippet in MySQL
    // - findAll()        -> Get all snippets from MySQL
    // - findById(id)     -> Get a snippet by its unique ID
    // - deleteById(id)   -> Delete a snippet by its unique ID
}
```

---

## 2. The Head Chef: `SnippetService.java`

Now we create the **Chef** (`SnippetService`). The chef is where the actual work and rules happen.

**File:** `src/main/java/com/example/codevault_backend/SnippetService.java`

```java
package com.example.codevault_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service tells Spring Boot: "This class is the Head Chef!"
@Service
public class SnippetService {

    // @Autowired is like a magic spell: "Spring Boot, automatically hand me the SnippetRepository!"
    @Autowired
    private SnippetRepository snippetRepository;

    // Save a new code snippet
    public Snippet saveSnippet(Snippet snippet) {
        return snippetRepository.save(snippet);
    }

    // Get all code snippets
    public List<Snippet> getAllSnippets() {
        return snippetRepository.findAll();
    }

    // Get a snippet by ID
    public Optional<Snippet> getSnippetById(Long id) {
        return snippetRepository.findById(id);
    }

    // Delete a snippet by ID
    public boolean deleteSnippet(Long id) {
        if (snippetRepository.existsById(id)) {
            snippetRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

---

## 3. The Waiter: `SnippetController.java`

The waiter sits at the front window taking HTTP requests from users and calling the Chef.

**File:** `src/main/java/com/example/codevault_backend/SnippetController.java`

```java
package com.example.codevault_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/snippets")
@CrossOrigin(origins = "*")
public class SnippetController {

    // Plug in our Head Chef
    @Autowired
    private SnippetService snippetService;

    // 1. Save Snippet (POST)
    @PostMapping
    public Snippet createSnippet(@RequestBody Snippet snippet) {
        return snippetService.saveSnippet(snippet);
    }

    // 2. Get All Snippets (GET)
    @GetMapping
    public List<Snippet> getAllSnippets() {
        return snippetService.getAllSnippets();
    }

    // 3. Get Snippet by ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Snippet> getSnippetById(@PathVariable Long id) {
        Optional<Snippet> snippet = snippetService.getSnippetById(id);
        return snippet.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Delete Snippet (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnippet(@PathVariable Long id) {
        if (snippetService.deleteSnippet(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

---

## 🍕 Explaining Annotations Like You're 10 Years Old!

### 🧑‍🍳 `@Service`
Imagine you own a toy factory. You don't build toys yourself—you hire a **Specialist Master Builder**. `@Service` tells Spring Boot: *"Hey! This person is a specialist master builder (Chef). Keep them ready in the factory because we will need them to build and recipe things!"*

### 🪄 `@Autowired`
Imagine whenever you are sitting at your desk, a magic robotic arm automatically places your pencil right into your hand without you having to stand up, go to the store, and buy a pencil (`new Object()`). `@Autowired` tells Spring Boot: *"Find the exact tool I need (like `SnippetRepository` or `SnippetService`) and plug it right in for me automatically!"*

---

## 🌐 The Full Flow of an API Request

```
Customer (Web / cURL) 
    ⬇️  HTTP POST /api/snippets
SnippetController (@RestController - Waiter takes order)
    ⬇️  calls saveSnippet()
SnippetService (@Service - Chef prepares order)
    ⬇️  calls save()
SnippetRepository (@Repository - Pantry Manager updates database)
    ⬇️  SQL INSERT
MySQL Database (Pantry stores item)
```
