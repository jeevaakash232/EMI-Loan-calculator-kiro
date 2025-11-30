#!/bin/bash

echo "Testing EMI Calculator Web Server on http://localhost:6000"
echo "============================================================"
echo ""

# Test home page
echo "1. Testing Home Page..."
curl -s http://localhost:6000/ > /dev/null && echo "✓ Home page is accessible" || echo "✗ Home page failed"

# Test calculate page
echo "2. Testing Calculate Page..."
curl -s http://localhost:6000/calculate > /dev/null && echo "✓ Calculate page is accessible" || echo "✗ Calculate page failed"

# Test history page
echo "3. Testing History Page..."
curl -s http://localhost:6000/history > /dev/null && echo "✓ History page is accessible" || echo "✗ History page failed"

# Test API - Calculate EMI
echo "4. Testing API - Calculate EMI..."
RESULT=$(curl -s -X POST http://localhost:6000/api/calculate -d "principal=500000&rate=8.5&tenure=60")
echo "   Response: $RESULT"

# Test API - Get History
echo "5. Testing API - Get History..."
HISTORY=$(curl -s http://localhost:6000/api/history)
echo "   Response: $HISTORY"

echo ""
echo "============================================================"
echo "Web server is running at: http://localhost:6000"
echo "Open this URL in your browser to use the EMI Calculator"
