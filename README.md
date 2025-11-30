# Loan EMI Calculator

A Java-based loan EMI (Equated Monthly Installment) calculator with calculation history stored in SQLite database.

## Features

- Calculate monthly EMI for loans
- View total amount payable and total interest
- Store calculation history in SQLite database
- View past calculations

## EMI Formula

```
EMI = [P x R x (1+R)^N] / [(1+R)^N-1]
```

Where:
- P = Principal loan amount
- R = Monthly interest rate (Annual rate / 12 / 100)
- N = Loan tenure in months

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Build and Run

1. Build the project:
```bash
mvn clean compile
```

2. Run the application:
```bash
mvn exec:java -Dexec.mainClass="com.emicalculator.EMICalculatorApp"
```

Alternatively, compile and run directly:
```bash
mvn clean package
java -cp target/loan-emi-calculator-1.0.0.jar com.emicalculator.EMICalculatorApp
```

## Usage

1. Choose option 1 to calculate new EMI
2. Enter loan amount, interest rate, and tenure
3. View the calculated EMI and total amounts
4. Choose option 2 to view calculation history
5. Choose option 3 to exit

## Database

The application uses SQLite database (`emi_calculator.db`) to store calculation history. The database file is created automatically in the project root directory.
