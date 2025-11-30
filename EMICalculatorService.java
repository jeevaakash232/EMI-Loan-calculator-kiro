package com.emicalculator.service;

import com.emicalculator.model.EMICalculation;

public class EMICalculatorService {

    /**
     * Calculate EMI using the formula:
     * EMI = [P x R x (1+R)^N] / [(1+R)^N-1]
     * where P = Principal, R = Monthly Interest Rate, N = Tenure in months
     */
    public EMICalculation calculateEMI(double principal, double annualRate, int tenureMonths) {
        EMICalculation calculation = new EMICalculation(principal, annualRate, tenureMonths);
        
        // Convert annual rate to monthly rate
        double monthlyRate = annualRate / (12 * 100);
        
        double emi;
        if (monthlyRate == 0) {
            // If interest rate is 0, EMI is simply principal divided by tenure
            emi = principal / tenureMonths;
        } else {
            // EMI formula
            double temp = Math.pow(1 + monthlyRate, tenureMonths);
            emi = (principal * monthlyRate * temp) / (temp - 1);
        }
        
        double totalAmount = emi * tenureMonths;
        double totalInterest = totalAmount - principal;
        
        calculation.setEmiAmount(Math.round(emi * 100.0) / 100.0);
        calculation.setTotalAmount(Math.round(totalAmount * 100.0) / 100.0);
        calculation.setTotalInterest(Math.round(totalInterest * 100.0) / 100.0);
        
        return calculation;
    }
}
