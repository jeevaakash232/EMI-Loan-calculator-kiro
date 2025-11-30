package com.emicalculator;

import com.emicalculator.database.DatabaseManager;
import com.emicalculator.model.EMICalculation;
import com.emicalculator.service.EMICalculatorService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class EMICalculatorApp {
    private static final EMICalculatorService calculatorService = new EMICalculatorService();
    private static final DatabaseManager databaseManager = new DatabaseManager();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("  Loan EMI Calculator");
        System.out.println("=================================\n");

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    calculateNewEMI();
                    break;
                case 2:
                    viewHistory();
                    break;
                case 3:
                    running = false;
                    System.out.println("\nThank you for using EMI Calculator!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }

        databaseManager.close();
        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("1. Calculate New EMI");
        System.out.println("2. View Calculation History");
        System.out.println("3. Exit");
        System.out.println();
    }

    private static void calculateNewEMI() {
        System.out.println("\n--- New EMI Calculation ---");
        
        double principal = getDoubleInput("Enter loan amount (Principal): ");
        double annualRate = getDoubleInput("Enter annual interest rate (%): ");
        int tenureMonths = getIntInput("Enter loan tenure (in months): ");

        EMICalculation calculation = calculatorService.calculateEMI(principal, annualRate, tenureMonths);
        
        displayCalculationResult(calculation);
        
        databaseManager.saveCalculation(calculation);
        System.out.println("\n✓ Calculation saved to history.\n");
    }

    private static void viewHistory() {
        System.out.println("\n--- Calculation History ---");
        
        List<EMICalculation> history = databaseManager.getCalculationHistory();
        
        if (history.isEmpty()) {
            System.out.println("No calculation history found.\n");
            return;
        }

        System.out.println(String.format("%-5s %-12s %-8s %-10s %-12s %-12s %-12s %-20s",
                "ID", "Principal", "Rate%", "Tenure", "EMI", "Total Amt", "Interest", "Date"));
        System.out.println("=".repeat(110));

        for (EMICalculation calc : history) {
            System.out.println(String.format("%-5d %-12.2f %-8.2f %-10d %-12.2f %-12.2f %-12.2f %-20s",
                    calc.getId(),
                    calc.getPrincipal(),
                    calc.getRateOfInterest(),
                    calc.getTenureMonths(),
                    calc.getEmiAmount(),
                    calc.getTotalAmount(),
                    calc.getTotalInterest(),
                    calc.getCalculatedAt().format(formatter)));
        }
        System.out.println();
    }

    private static void displayCalculationResult(EMICalculation calculation) {
        System.out.println("\n--- EMI Calculation Result ---");
        System.out.println("Loan Amount (Principal): ₹" + String.format("%.2f", calculation.getPrincipal()));
        System.out.println("Interest Rate: " + calculation.getRateOfInterest() + "% per annum");
        System.out.println("Loan Tenure: " + calculation.getTenureMonths() + " months");
        System.out.println("----------------------------");
        System.out.println("Monthly EMI: ₹" + String.format("%.2f", calculation.getEmiAmount()));
        System.out.println("Total Amount Payable: ₹" + String.format("%.2f", calculation.getTotalAmount()));
        System.out.println("Total Interest: ₹" + String.format("%.2f", calculation.getTotalInterest()));
        System.out.println("----------------------------");
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}
