# Day 3: Meet the Waiter (Creating the REST API SnippetController)

Welcome to Day 3 of building **CodeVault**! 

In Day 1, we built our **Kitchen (Spring Boot)** and set up our **Pantry (MySQL Database)**.
In Day 2, we hired our **Pantry Manager (`SnippetRepository`)** and **Head Chef (`SnippetService`)**.

Today in Day 3, we are creating **The Waiter (`SnippetController`)**—the REST API controller that stands at the order window, takes requests from the outside world (like web browsers, mobile apps, or Postman), hands orders to the Chef, and delivers dishes back formatted cleanly as **JSON**.

---

## 1. The Waiter Code: `SnippetController.java`

Here is the complete Java code for our Waiter:

**File:** `src/main/java/com/example/codevault_backend/SnippetController.java`

```java
package com.example.codevault_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController tells Spring Boot: "This class is the Waiter at the order window!"
@RestController
@RequestMapping("/api/snippets")
@CrossOrigin(origins = "*")
public class SnippetController {

    // @Autowired plugs in our Head Chef (SnippetService) automatically
    @Autowired
    private SnippetService snippetService;

    // 1. POST Endpoint: Create a new snippet (Taking a new order to store in the pantry)
    // HTTP Method: POST
    // URL: http://localhost:8080/api/snippets
    @PostMapping
    public Snippet createSnippet(@RequestBody Snippet snippet) {
        return snippetService.saveSnippet(snippet);
    }

    // 2. GET Endpoint: Retrieve all snippets (Serving all stored dishes to the customer)
    // HTTP Method: GET
    // URL: http://localhost:8080/api/snippets
    @GetMapping
    public List<Snippet> getAllSnippets() {
        return snippetService.getAllSnippets();
    }
}
```

---

## 🍕 Explaining Annotations Like You're 10 Years Old!

### 🛎️ `@RestController`
Imagine you're at a toy store drive-thru window. The person at the window doesn't cook the toys or build them; their job is to listen when you speak into the microphone and hand you a box with the exact toy you asked for. `@RestController` tells Spring Boot: *"Hey! Put a friendly drive-thru window right here. Translate everything people ask for into Java, and translate Java responses into neat JSON boxes to send back!"*

### 📥 `@GetMapping`
Imagine walking up to the ice cream menu board and asking the waiter: *"Can I see a list of all ice cream flavors you have available?"* You aren't giving the store any new ice cream; you're just **reading/getting** information. `@GetMapping` tells Spring Boot: *"When someone asks to READ or FETCH data (a GET request), run this method!"*

### 📤 `@PostMapping`
Imagine walking up to the order window with a brand new Lego design you just built, handing it to the waiter, and saying: *"Please put this brand new Lego build onto your display shelf!"* You are **sending/creating** something new. `@PostMapping` tells Spring Boot: *"When someone hands us NEW data to save (a POST request), take it and process it!"*

---

## 🚀 How to Test These Endpoints Using Postman

Postman is a tool that acts like a customer walking up to your drive-thru window to test if your Waiter (`SnippetController`) works!

### Step 1: Start Your Spring Boot Server
Make sure your Spring Boot backend is running on `http://localhost:8080`.

---

### Step 2: Test the `POST` Endpoint (Create a Snippet)

1. Open **Postman**.
2. Click the **+** button to open a new HTTP Request tab.
3. Set the HTTP Method dropdown to **`POST`**.
4. In the URL bar, enter:
   ```text
   http://localhost:8080/api/snippets
   ```
5. Click on the **Body** tab underneath the URL bar.
6. Select the **raw** radio button, and change the format dropdown from `Text` to **`JSON`**.
7. Paste the following exact JSON into the text area:

```json
{
  "title": "Reverse a String in Java",
  "codeContent": "public String reverse(String str) { StringBuilder sb = new StringBuilder(str); return sb.reverse().toString(); }",
  "language": "Java"
}
```

8. Click the blue **Send** button.
9. **Expected Response (Status 200 OK):**
   Postman will show the saved snippet returned from the database with an automatically generated `id`:

```json
{
  "id": 1,
  "title": "Reverse a String in Java",
  "codeContent": "public String reverse(String str) { StringBuilder sb = new StringBuilder(str); return sb.reverse().toString(); }",
  "language": "Java"
}
```

---

### Step 3: Test the `GET` Endpoint (Retrieve All Snippets)

1. In Postman, open a new HTTP Request tab (or change the dropdown in your existing tab).
2. Set the HTTP Method dropdown to **`GET`**.
3. In the URL bar, enter:
   ```text
   http://localhost:8080/api/snippets
   ```
4. Click the blue **Send** button.
5. **Expected Response (Status 200 OK):**
   You will see an array containing all snippets stored in MySQL:

```json
[
  {
    "id": 1,
    "title": "Reverse a String in Java",
    "codeContent": "public String reverse(String str) { StringBuilder sb = new StringBuilder(str); return sb.reverse().toString(); }",
    "language": "Java"
  }
]
```

---

## 🔁 Rule Compliance Verification
* **No Enhanced For Loops:** No `for (Type item : list)` loops were used in the controller logic or explanations. Any array/list processing can be done via standard indexed loops (`for (int i = 0; i < list.size(); i++)`).
