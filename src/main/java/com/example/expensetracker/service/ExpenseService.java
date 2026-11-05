package com.example.expensetracker.service;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.repository.ExpenseRepository;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense addExpense(Expense expense) {
        return repository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return repository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expense not found"));
    }

    public Expense updateExpense(Long id, Expense updated) {
        Expense existing = getExpenseById(id);

        existing.setDescription(updated.getDescription());
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setDate(updated.getDate());

        return repository.save(existing);
    }

    public void deleteExpense(Long id) {
        Expense existing = getExpenseById(id);
        repository.delete(existing);
    }

    public BigDecimal getTotalExpenses() {
        return repository.findAll().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return repository.findByCategoryIgnoreCase(category);
    }
}
