#!/bin/bash

# Ensure you are in the project directory
cd "$(dirname "$0")"  # Navigate to the script's directory

# Step 1: Start the Spring Boot Application (this assumes you already built the JAR)
echo "Starting Spring Boot Application..."
java -jar api/build/libs/api-1.0-SNAPSHOT.jar &

# Step 2: Start the Spark Shell commands (assuming classpath is set)
echo "Running Spark commands..."

# Step 3: Open the frontend webpage (assuming the Spring Boot app is running at localhost:9090)
# This opens the browser and goes to the page
echo "Opening browser to visualize the webpage at http://localhost:9090"
xdg-open "http://localhost:9090"  # For Linux; Use "open" for macOS, or "start" for Windows

# Optional: Tail the Spring Boot log file (this shows you logs in the terminal)
tail -f /path/to/your/spring-boot/logs/spring-boot.log
