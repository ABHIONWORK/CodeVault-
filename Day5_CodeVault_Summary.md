# Day 5: Adding Tags & Building the Search Feature

Welcome to Day 5 of building **CodeVault**! 

Today we added **Tags** (like `sorting`, `arrays`, `algorithms`) to code snippets and implemented a full-stack **Search Feature** connecting our React Dining Room all the way to our MySQL Pantry!

---

## 1. Updating Java Entity (`Snippet.java`)

We added a new `tags` String field to our entity blueprint:

```java
@Entity
public class Snippet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String codeContent;
    private String language;
    
    // 🏷️ Added tags field
    private String tags;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
```

---

## 2. Custom JPA Repository Search (`SnippetRepository.java`)

We added a derived query method that automatically generates case-insensitive SQL searching across both `title` and `tags`:

```java
@Repository
public interface SnippetRepository extends JpaRepository<Snippet, Long> {

    // Spring Data JPA automatically converts this method name into SQL:
    // SELECT * FROM snippet WHERE LOWER(title) LIKE %keyword% OR LOWER(tags) LIKE %keyword%;
    List<Snippet> findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(String titleKey, String tagsKey);
}
```

---

## 3. Service Layer with Traditional Indexing Loops (`SnippetService.java`)

To comply with traditional indexing loop rules, any manual array processing uses standard index loops `for (int i = 0; i < list.size(); i++)`:

```java
@Service
public class SnippetService {

    @Autowired
    private SnippetRepository snippetRepository;

    public List<Snippet> searchSnippets(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return snippetRepository.findAll();
        }
        return snippetRepository.findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase(keyword, keyword);
    }

    // Traditional indexing loop implementation for explicit in-memory filtering:
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
}
```

---

## 4. REST Controller Endpoint (`SnippetController.java`)

Updated the `@GetMapping` endpoint to accept an optional `@RequestParam(required = false) String search`:

```java
@RestController
@RequestMapping("/api/snippets")
@CrossOrigin(origins = "*")
public class SnippetController {

    @Autowired
    private SnippetService snippetService;

    @GetMapping
    public List<Snippet> getAllSnippets(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return snippetService.searchSnippets(search);
        }
        return snippetService.getAllSnippets();
    }
}
```

---

## 5. React Search Bar UI (`App.jsx`)

Added `searchQuery` state and connected `useEffect` to re-fetch snippets from `http://localhost:8080/api/snippets?search=...` whenever the user types:

```jsx
const [searchQuery, setSearchQuery] = useState('')

useEffect(() => {
  const url = searchQuery.trim()
    ? `http://localhost:8080/api/snippets?search=${encodeURIComponent(searchQuery)}`
    : 'http://localhost:8080/api/snippets'

  fetch(url)
    .then((res) => res.json())
    .then((data) => setSnippets(data))
}, [searchQuery])
```

---

## 🌐 Complete Data Flow Explanation

```mermaid
sequenceDiagram
    autonumber
    actor User as User in React App
    participant React as React Search Bar (App.jsx)
    participant Controller as SnippetController (@GetMapping)
    participant Service as SnippetService (@Service)
    participant Repo as SnippetRepository (JPA)
    participant DB as MySQL Database

    User->>React: Types "sorting" in Search Input
    Note over React: `onChange` updates `searchQuery` state.<br/>`useEffect` triggers fetch GET request.
    React->>Controller: GET /api/snippets?search=sorting
    Note over Controller: `@RequestParam String search` extracts "sorting"
    Controller->>Service: Calls `snippetService.searchSnippets("sorting")`
    Note over Service: Validates keyword.<br/>Or runs traditional `for(int i=0; i<list.size(); i++)` loop filter.
    Service->>Repo: Calls `findByTitleContainingIgnoreCaseOrTagsContainingIgnoreCase("sorting", "sorting")`
    Repo->>DB: Executes SQL:<br/>SELECT * FROM snippet WHERE LOWER(title) LIKE '%sorting%' OR LOWER(tags) LIKE '%sorting%'
    DB-->>Repo: Returns matching rows
    Repo-->>Service: Maps SQL rows to List<Snippet>
    Service-->>Controller: Returns List<Snippet>
    Controller-->>React: Responds with JSON array of matching snippets
    React-->>User: `setSnippets(data)` updates state & re-renders snippet grid!
```

---

## 🔁 Rule Compliance Verification
* **Traditional Index Loops Only:** Handled Java list iterations explicitly using index-based `for (int i = 0; i < list.size(); i++)` loops. No enhanced for-loops (`for (Type x : list)`) were used.
