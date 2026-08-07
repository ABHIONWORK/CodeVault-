# Day 8: Containerized Deployment with Docker & Docker Compose

Welcome to Day 8 of building **CodeVault**—the final chapter of our full-stack journey! 🚀

Today, we containerized our entire application (MySQL Database, Spring Boot Backend, and React Frontend) so that CodeVault can be deployed seamlessly onto any computer or cloud server in the world!

---

## 🚚 What is Docker? (Explained Like You're 10 Years Old!)

### 🍔 The Food Truck Metaphor

Imagine you build a fantastic restaurant in your kitchen at home. It cooks great meals on your specific stove, using your specific brand of pots and spices.

Now, imagine you want to take your restaurant to another town, or put it on a cloud server:
- If you move into a new building, you might find out they have a different stove, missing utensils, or don't have Java or Node installed!
- **Docker** solves this problem by packing your **ENTIRE restaurant**—the stove, utensils, Java version, Node runtime, code, and secret recipes—into a **portable Food Truck (Container)**!

Now, it doesn't matter if you park your Food Truck on Windows, Mac, Linux, or AWS cloud—**the truck carries everything it needs, so it works identically everywhere!**

---

### 📦 Key Docker Concepts

| Term | Analogy | Description |
| :--- | :--- | :--- |
| **Dockerfile** | **Assembly Manual** | Step-by-step instructions to build the Food Truck container image. |
| **Docker Image** | **Blueprint / Template** | Read-only snapshot containing the code, environment, and dependencies. |
| **Docker Container** | **Living Food Truck** | The running instance of a Docker image. |
| **Docker Compose** | **Traffic Controller / Fleet Manager** | Parks and connects all 3 Food Trucks (MySQL, Spring Boot, React) together on a private network. |

---

## 📄 Docker Files Walkthrough

### 1. Backend Dockerfile (`codevault-backend/Dockerfile`)
Uses a 2-stage build: Stage 1 builds the Java JAR using Maven, and Stage 2 runs the lightweight JAR file inside JRE 21.

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### 2. Frontend Dockerfile (`codevault-frontend/Dockerfile`)
Uses a 2-stage build: Stage 1 builds static production assets using Node.js, and Stage 2 serves them using NGINX web server.

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

---

### 3. Orchestration with Docker Compose (`docker-compose.yml`)

Connects all three containers on a shared bridge network:

```yaml
version: '3.8'

services:
  mysql-db:
    image: mysql:8.0
    container_name: codevault-db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: codevault
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - codevault-network

  backend:
    build:
      context: ./codevault-backend
      dockerfile: Dockerfile
    container_name: codevault-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/codevault
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      - mysql-db
    networks:
      - codevault-network

  frontend:
    build:
      context: ./codevault-frontend
      dockerfile: Dockerfile
    container_name: codevault-frontend
    ports:
      - "5173:80"
    depends_on:
      - backend
    networks:
      - codevault-network

networks:
  codevault-network:
    driver: bridge

volumes:
  mysql_data:
```

---

## ⚡ Commands to Run the Whole Project

Open your terminal at the root directory of your project (`/Users/abhishekkumar/CodeVault Resume`) and run:

### 1. Build & Start All Containers
```bash
docker compose up --build
```
> *(This command builds the images, starts MySQL, starts Spring Boot, starts React, and wires their networks together automatically!)*

### 2. Run in Detached Mode (Background)
```bash
docker compose up -d --build
```

### 3. Check Status of Running Containers
```bash
docker compose ps
```

### 4. Stop and Remove Containers
```bash
docker compose down
```

---

## 🏆 Project Completion & Resume Defense

Congratulations! You have completed the 8-Day **CodeVault** Full-Stack Journey:

- **Day 1:** Spring Boot Initialization & MySQL Entity Blueprinting
- **Day 2:** Controller, Service, Repository Architecture
- **Day 3:** REST APIs (`@GetMapping`, `@PostMapping`) & Postman Verification
- **Day 4:** React.js Frontend Initialization & CORS Security Resolution
- **Day 5:** Entity Tagging, JPA Keyword Search & End-to-End Data Flow
- **Day 6:** Spring Security Bouncer & JWT Authentication Engine
- **Day 7:** React Authentication, Token Persistence & AI Suggestion Module
- **Day 8:** Docker Containerization & Docker Compose Deployment
