package com.simple.expense;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final String DATA_PATH = System.getProperty("user.home") + "/.simple-expenses/expenses.csv";

    public static void main(String[] args) {
        Storage storage = new Storage(Paths.get(DATA_PATH));
        List<Expense> expenses = storage.loadAll();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Simple Expense Tracker (vanilla Java)");
        printHelp();

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;
            String cmd = line.split("\\s+")[0].toLowerCase();

            try {
                switch (cmd) {
                    case "add":
                        doAdd(scanner, expenses, storage);
                        break;
                    case "list":
                        doList(expenses);
                        break;
                    case "total":
                        doTotal(expenses);
                        break;
                    case "delete":
                        doDelete(scanner, expenses, storage);
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "exit":
                        System.out.println("Bye.");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Unknown command. Type 'help' for commands.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("Commands: add | list | total | delete | help | exit");
        System.out.println("  add    - add an expense (prompts for fields)");
        System.out.println("  list   - show all expenses with index");
        System.out.println("  total  - show sum of all expenses");
        System.out.println("  delete - delete an expense by index (use 'list' to see indexes)");
    }

    private static void doAdd(Scanner sc, List<Expense> list, Storage storage) {
        System.out.print("Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(sc.nextLine().trim());
        System.out.print("Amount: ");
        double amount = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Category: ");
        String category = sc.nextLine().trim();
        System.out.print("Description: ");
        String description = sc.nextLine().trim();
        Expense e = new Expense(date, amount, category, description);
        list.add(e);
        storage.saveAll(list);
        System.out.println("Added.");
    }

    private static void doList(List<Expense> list) {
        if (list.isEmpty()) {
            System.out.println("No expenses recorded.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.printf("%d: %s%n", i, list.get(i).toString());
        }
    }

    private static void doTotal(List<Expense> list) {
        double sum = list.stream().mapToDouble(Expense::getAmount).sum();
        System.out.printf("Total expenses: %.2f%n", sum);
    }

    private static void doDelete(Scanner sc, List<Expense> list, Storage storage) {
        if (list.isEmpty()) {
            System.out.println("No expenses to delete.");
            return;
        }
        doList(list);
        System.out.print("Enter index to delete: ");
        int idx = Integer.parseInt(sc.nextLine().trim());
        if (idx < 0 || idx >= list.size()) {
            System.out.println("Invalid index.");
            return;
        }
        Expense removed = list.remove(idx);
        storage.saveAll(list);
        System.out.println("Deleted: " + removed);
    }
}
