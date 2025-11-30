package com.emicalculator.model;

import java.time.LocalDateTime;

public class EMICalculation {
    private int id;
    private double principal;
    private double rateOfInterest;
    private int tenureMonths;
    private double emiAmount;
    private double totalAmount;
    private double totalInterest;
    private LocalDateTime calculatedAt;

    public EMICalculation() {
    }

    public EMICalculation(double principal, double rateOfInterest, int tenureMonths) {
        this.principal = principal;
        this.rateOfInterest = rateOfInterest;
        this.tenureMonths = tenureMonths;
        this.calculatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getRateOfInterest() { return rateOfInterest; }
    public void setRateOfInterest(double rateOfInterest) { this.rateOfInterest = rateOfInterest; }

    public int getTenureMonths() { return tenureMonths; }
    public void setTenureMonths(int tenureMonths) { this.tenureMonths = tenureMonths; }

    public double getEmiAmount() { return emiAmount; }
    public void setEmiAmount(double emiAmount) { this.emiAmount = emiAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTotalInterest() { return totalInterest; }
    public void setTotalInterest(double totalInterest) { this.totalInterest = totalInterest; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
