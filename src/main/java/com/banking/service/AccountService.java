package com.banking.service;

import com.banking.BankingSystem;
import com.banking.model.Account;
import com.banking.model.AccountFactory;
import com.banking.model.AccountType;
import com.banking.exception.*;
import com.banking.util.TransactionLogger;
import java.math.BigDecimal;
import java.util.List;

/**
 * This class handles all banking operations like:
 * - Creating new accounts
 * - Depositing money
 * - Withdrawing money
 * - Transferring between accounts
 */
public class AccountService {
    // We need these to work with accounts and save transactions
    private final BankingSystem bankingSystem;
    private final TransactionLogger logger;

    // When we create AccountService, we need a BankingSystem
    public AccountService(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
        this.logger = new TransactionLogger();
    }

    /**
     * Create a new bank account
     */
    public Account createAccount(AccountType type, String accountId, BigDecimal initialBalance)
            throws BankingException {
        // Check that initial balance is positive
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BankingException("Initial balance cannot be negative");
        }

        // Create the account
        Account account = AccountFactory.createAccount(type, accountId, initialBalance);

        // Save it in the banking system
        bankingSystem.addAccount(account);

        // Log the initial deposit
        logger.saveTransaction(accountId, initialBalance);

        return account;
    }

    /**
     * Deposit money into an account
     */
    public void deposit(String accountId, BigDecimal amount) throws BankingException {
        // Check that deposit amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Deposit amount must be positive");
        }

        // Find the account
        Account account = findAccount(accountId);

        // Add the money
        account.deposit(amount);

        // Save the transaction
        logger.saveTransaction(accountId, amount);
    }

    /**
     * Withdraw money from an account
     */
    public void withdraw(String accountId, BigDecimal amount) throws BankingException {
        // Check that withdrawal amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Withdrawal amount must be positive");
        }

        // Find the account
        Account account = findAccount(accountId);

        // Take out the money
        account.withdraw(amount);

        // Save the transaction (negative amount for withdrawal)
        logger.saveTransaction(accountId, amount.negate());
    }

    /**
     * Transfer money between accounts
     */
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount)
            throws BankingException {
        // Check that transfer amount is positive
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankingException("Transfer amount must be positive");
        }

        // Find both accounts
        Account fromAccount = findAccount(fromAccountId);
        Account toAccount = findAccount(toAccountId);

        // Take money from first account
        fromAccount.withdraw(amount);

        // Add money to second account
        toAccount.deposit(amount);

        // Save both transactions
        logger.saveTransaction(fromAccountId, amount.negate());
        logger.saveTransaction(toAccountId, amount);
    }

    /**
     * Get the balance of an account
     */
    public BigDecimal getBalance(String accountId) throws AccountNotFoundException {
        Account account = findAccount(accountId);
        return account.getBalance();
    }

    /**
     * Get transaction history for an account
     */
    public List<String> getTransactionHistory(String accountId) {
        return logger.getTransactionsForAccount(accountId);
    }

    /**
     * Get all transactions in the system
     */
    public List<String> getAllTransactions() {
        return logger.getAllTransactions();
    }

    /**
     * Helper method to find an account
     */
    private Account findAccount(String accountId) throws AccountNotFoundException {
        // Make sure account ID is valid
        if (accountId == null || accountId.isEmpty()) {
            throw new AccountNotFoundException("Account number cannot be empty");
        }

        // Try to find the account
        return bankingSystem.findAccount(accountId);
    }
}
