# Day 6: Adding the Bouncer (Spring Security & JWT Authentication)

Welcome to Day 6 of building **CodeVault**! 

Today we added the **Bouncer (Spring Security & JWT Authentication)** to protect our backend kitchen!

---

## 🎟️ What is a JWT? (Explained Like You're 10 Years Old!)

Imagine going to an **Amusement Park**:
- At the front gate, you show your birth certificate and ticket to the ticket booth (logging in with username and password).
- Once the ticket booth verifies who you are, they **stamp your hand with a special invisible ink stamp** (or give you a **VIP Wristband**). That stamp is your **JWT (JSON Web Token)**!
- Now, whenever you want to ride a roller coaster (make a `POST` request to create a snippet), you don't have to carry your birth certificate every single time! You simply **flash your hand stamp** to the ride attendant (the Security Bouncer).
- The bouncer checks the stamp to make sure it's real, hasn't expired, and hasn't been forged. If valid, you ride!

---

## 🛠️ Step-by-Step Code Walkthrough

### 1. `JwtUtil.java` (The Stamp Maker)
Generates tokens upon successful login and validates them on incoming requests.

```java
@Component
public class JwtUtil {
    private static final String SECRET_KEY_STRING = "CodeVaultSuperSecretKeyForJWTAuthentication2026!";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }
}
```

---

### 2. `JwtRequestFilter.java` (The Bouncer's Assistant)
Inspects every incoming HTTP request looking for `Authorization: Bearer <token>`.

```java
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwtToken);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwtToken, username)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

---

### 3. `SecurityConfig.java` (The Security Rulebook)
Configures which endpoints are public (`GET /api/snippets`, `POST /api/auth/login`) and which require a VIP wristband (`POST /api/snippets`).

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/snippets/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/snippets").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/snippets/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

---

### 4. `AuthController.java` (The Ticket Booth)
Exposes `POST /api/auth/login` where users submit credentials to receive a token.

```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        if ("admin".equals(authRequest.getUsername()) && "password123".equals(authRequest.getPassword())) {
            String token = jwtUtil.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
```

---

## 🧪 How to Test Security in Postman

1. **Step 1: Try Creating a Snippet Without Logging In**
   - `POST http://localhost:8080/api/snippets`
   - **Response:** `401 Unauthorized` ❌ (Bouncer blocks you!)

2. **Step 2: Log In to Get Your JWT Wristband**
   - `POST http://localhost:8080/api/auth/login`
   - Body (`raw JSON`):
     ```json
     {
       "username": "admin",
       "password": "password123"
     }
     ```
   - **Response:** Returns your token string!

3. **Step 3: Create Snippet With Token**
   - `POST http://localhost:8080/api/snippets`
   - Headers: Add `Authorization` header with value `Bearer <YOUR_COPIED_TOKEN>`
   - **Response:** `200 OK` (Snippet created successfully! 🎉)
