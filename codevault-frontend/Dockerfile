# Step 1: Build Stage using Node.js
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install --fetch-retries=5 --fetch-retry-mintimeout=20000


# Copy source code and build production bundles
COPY . .
RUN npm run build

# Step 2: Production Stage using lightweight NGINX Web Server
FROM nginx:alpine

# Copy built static files from React build to NGINX default serving folder
COPY --from=build /app/dist /usr/share/nginx/html

# Expose HTTP port 80
EXPOSE 80

# Run NGINX server in the foreground
CMD ["nginx", "-g", "daemon off;"]
