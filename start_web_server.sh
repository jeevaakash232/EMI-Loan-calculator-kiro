#!/bin/bash

# Kill any existing Java processes on port 6000
lsof -ti:6000 | xargs kill -9 2>/dev/null || true

# Clean and recompile
rm -rf out
mkdir -p out

# Compile all Java files
javac -d out -cp lib/sqlite-jdbc-3.42.0.0.jar \
  src/main/java/com/emicalculator/model/EMICalculation.java \
  src/main/java/com/emicalculator/service/EMICalculatorService.java \
  src/main/java/com/emicalculator/database/DatabaseManager.java \
  src/main/java/com/emicalculator/web/EMIWebServer.java

# Run the web server
java -cp out:lib/sqlite-jdbc-3.42.0.0.jar com.emicalculator.web.EMIWebServer
