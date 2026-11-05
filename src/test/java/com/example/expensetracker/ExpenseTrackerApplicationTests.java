
package com.example.expensetracker;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.service.ExpenseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpenseTrackerApplicationTests {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @BeforeEach
    void setUp() {
        expenseRepository.deleteAll();
    }

    @Test
    void testAddExpense() {
        Expense expense = new Expense(
                "Lunch",
                new BigDecimal("250"),
                "Food",
                LocalDate.now()
        );

        Expense saved = expenseService.addExpense(expense);

        assertNotNull(saved.getId());
        assertEquals("Lunch", saved.getDescription());
    }

    @Test
    void testGetAllExpenses() {
        expenseService.addExpense(new Expense(
                "Lunch", new BigDecimal("250"),
                "Food", LocalDate.now()
        ));

        expenseService.addExpense(new Expense(
                "Transport", new BigDecimal("100"),
                "Travel", LocalDate.now()
        ));

        List<Expense> expenses = expenseService.getAllExpenses();

        assertEquals(2, expenses.size());
    }

    @Test
    void testGetExpenseById() {
        Expense saved = expenseService.addExpense(new Expense(
                "Dinner", new BigDecimal("300"),
                "Food", LocalDate.now()
        ));

        Expense result = expenseService.getExpenseById(saved.getId());

        assertEquals("Dinner", result.getDescription());
    }

    @Test
    void testUpdateExpense() {
        Expense saved = expenseService.addExpense(new Expense(
                "Lunch", new BigDecimal("200"),
                "Food", LocalDate.now()
        ));

        Expense updated = new Expense(
                "Dinner", new BigDecimal("350"),
                "Food", LocalDate.now()
        );

        Expense result = expenseService.updateExpense(
                saved.getId(), updated
        );

        assertEquals("Dinner", result.getDescription());
        assertEquals(0,
                new BigDecimal("350").compareTo(result.getAmount()));
    }

    @Test
    void testDeleteExpense() {
        Expense saved = expenseService.addExpense(new Expense(
                "Coffee", new BigDecimal("80"),
                "Food", LocalDate.now()
        ));

        expenseService.deleteExpense(saved.getId());

        assertTrue(expenseRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    void testCalculateTotalExpenses() {
        expenseService.addExpense(new Expense(
                "Lunch", new BigDecimal("250"),
                "Food", LocalDate.now()
        ));

        expenseService.addExpense(new Expense(
                "Travel", new BigDecimal("150"),
                "Transport", LocalDate.now()
        ));

        BigDecimal total = expenseService.getTotalExpenses();

        assertEquals(0, new BigDecimal("400").compareTo(total));
    }
}
