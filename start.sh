#!/bin/bash

# Kill any existing processes
lsof -ti:8080 | xargs kill -9 2>/dev/null || true
lsof -ti:6000 | xargs kill -9 2>/dev/null || true
pkill -9 -f "EMIWebServer" 2>/dev/null || true

sleep 1

# Compile
rm -rf out
mkdir -p out

javac -d out -cp lib/sqlite-jdbc-3.42.0.0.jar \
  src/main/java/com/emicalculator/model/EMICalculation.java \
  src/main/java/com/emicalculator/service/EMICalculatorService.java \
  src/main/java/com/emicalculator/database/DatabaseManager.java \
  src/main/java/com/emicalculator/web/EMIWebServer.java

# Run on port 8080
java -cp out:lib/sqlite-jdbc-3.42.0.0.jar com.emicalculator.web.EMIWebServer
