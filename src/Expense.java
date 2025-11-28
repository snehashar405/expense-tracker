package com.simple.expense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Simple POJO representing an expense record.
 * CSV format used: date,amount,category,description
 */
public class Expense {
    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE;

    private LocalDate date;
    private double amount;
    private String category;
    private String description;

    public Expense(LocalDate date, double amount, String category, String description) {
        this.date = date;
        this.amount = amount;
        this.category = category == null ? "" : category;
        this.description = description == null ? "" : description;
    }

    // Used by Storage when reading CSV
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    public void setDate(LocalDate date) { this.date = date; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("%s | %.2f | %s | %s",
                date.format(F), amount, category, description);
    }

  //processing csv
    public String toCsvRow() {
        return String.join(",",
                date.format(F),
                String.valueOf(amount),
                safe(category),
                safe(description));
    }

    /*
     * Parse a CSV row (date,amount,category,description).
     * Useing split limit 4 so description may contain commas.
     */
    public static Expense fromCsvRow(String row) {
        String[] parts = row.split(",", 4);
        LocalDate d = LocalDate.parse(parts[0], F);
        double a = Double.parseDouble(parts[1]);
        String c = parts.length > 2 ? parts[2] : "";
        String desc = parts.length > 3 ? parts[3] : "";
        return new Expense(d, a, c, desc);
    }

    // Removing line breaks 
    private static String safe(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ");
    }
}
