# EMI Calculator Web Application

## Server Status: ✓ RUNNING

The EMI Calculator web application is now running on **http://localhost:6000**

## Features

### Web Interface
1. **Home Page** - http://localhost:6000/
   - Navigation to Calculate and History pages

2. **Calculate EMI** - http://localhost:6000/calculate
   - Enter loan amount, interest rate, and tenure
   - Get instant EMI calculation
   - Results automatically saved to database

3. **View History** - http://localhost:6000/history
   - View all past calculations in a table format
   - Shows complete details including date/time

### REST API Endpoints

1. **POST /api/calculate**
   - Calculate EMI and save to database
   - Parameters: principal, rate, tenure
   - Returns: JSON with emi, totalAmount, totalInterest

2. **GET /api/history**
   - Retrieve all calculation history
   - Returns: JSON array of all calculations

## Test Results

✓ Home page is accessible
✓ Calculate page is accessible  
✓ History page is accessible
✓ API Calculate endpoint working
✓ API History endpoint working

### Sample Calculation
- Loan Amount: ₹500,000
- Interest Rate: 8.5% per annum
- Tenure: 60 months
- **Monthly EMI: ₹10,258.27**
- Total Amount: ₹615,495.94
- Total Interest: ₹115,495.94

## How to Use

1. Open your web browser
2. Navigate to: **http://localhost:6000**
3. Click "Calculate EMI" to perform new calculations
4. Click "View History" to see past calculations

## Database

All calculations are automatically stored in SQLite database (`emi_calculator.db`)

## To Stop the Server

Run: `lsof -ti:6000 | xargs kill -9`
