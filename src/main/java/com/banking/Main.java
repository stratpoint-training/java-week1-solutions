package com.banking;

import com.banking.model.*;
import com.banking.service.AccountService;
import com.banking.exception.*;
import java.math.BigDecimal;

/**
 * A simple banking application that shows basic account operations.
 * This program demonstrates:
 * 1. How to create bank accounts
 * 2. How to deposit money
 * 3. How to withdraw money
 * 4. How to transfer between accounts
 * 5. How to handle errors
 */
public class Main {
    public static void main(String[] args) {
        // Create objects we need
        BankingSystem bankingSystem = new BankingSystem();
        AccountService accountService = new AccountService(bankingSystem);

        try {
            // First, let's create two accounts
            System.out.println("Creating two bank accounts...");

            // Create a savings account with $1000
            Account savings = accountService.createAccount(
                AccountType.SAVINGS,
                "SAV001",
                new BigDecimal("1000.00")
            );
            System.out.println("Created savings account with $1000");

            // Create a checking account with $2000
            Account checking = accountService.createAccount(
                AccountType.CHECKING,
                "CHK001",
                new BigDecimal("2000.00")
            );
            System.out.println("Created checking account with $2000");

            // Show how much money is in each account
            System.out.println("\nMoney in each account:");
            System.out.println("Savings: $" + accountService.getBalance("SAV001"));
            System.out.println("Checking: $" + accountService.getBalance("CHK001"));

            // Let's deposit some money
            System.out.println("\nDepositing $500 to savings...");
            accountService.deposit("SAV001", new BigDecimal("500.00"));
            System.out.println("Savings balance is now: $" + accountService.getBalance("SAV001"));

            // Let's withdraw some money
            System.out.println("\nWithdrawing $300 from checking...");
            accountService.withdraw("CHK001", new BigDecimal("300.00"));
            System.out.println("Checking balance is now: $" + accountService.getBalance("CHK001"));

            // Let's transfer money between accounts
            System.out.println("\nTransferring $200 from checking to savings...");
            accountService.transfer("CHK001", "SAV001", new BigDecimal("200.00"));
            System.out.println("After transfer:");
            System.out.println("Savings: $" + accountService.getBalance("SAV001"));
            System.out.println("Checking: $" + accountService.getBalance("CHK001"));

            // Now let's see what happens when we make mistakes
            System.out.println("\nTrying some operations that should fail:");

            // Try 1: Withdraw too much money
            System.out.println("\nTrying to withdraw too much money...");
            try {
                accountService.withdraw("SAV001", new BigDecimal("5000.00"));
            } catch (InsufficientFundsException e) {
                System.out.println("Failed: Not enough money in account");
            }

            // Try 2: Use an account that doesn't exist
            System.out.println("\nTrying to use an account that doesn't exist...");
            try {
                accountService.getBalance("ABC123");
            } catch (AccountNotFoundException e) {
                System.out.println("Failed: This account number doesn't exist");
            }

            // Try 3: Deposit a negative amount
            System.out.println("\nTrying to deposit a negative amount...");
            try {
                accountService.deposit("SAV001", new BigDecimal("-100.00"));
            } catch (BankingException e) {
                System.out.println("Failed: Can't deposit negative money");
            }

        } catch (BankingException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
}
