#!/bin/bash

# Test script to demonstrate EMI Calculator

echo "Testing EMI Calculator Application"
echo "==================================="
echo ""

# Test Case 1: Calculate EMI
echo "Test 1: Calculating EMI for loan of 500000, 8.5% interest, 60 months"
echo -e "1\n500000\n8.5\n60\n2\n3" | java -cp out:lib/sqlite-jdbc-3.42.0.0.jar com.emicalculator.EMICalculatorApp

echo ""
echo "==================================="
echo "Test completed successfully!"
