package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.ExpenseRequestDTO;
import com.nathannolacio.meusaldo.dto.ExpenseResponseDTO;
import com.nathannolacio.meusaldo.model.Expense;
import com.nathannolacio.meusaldo.security.CustomUserDetails;
import com.nathannolacio.meusaldo.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Operation(summary = "Lista todas as despesas do usuário logado")
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getUserExpenses(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        Long userId = user.getId();

        List<ExpenseResponseDTO> expenses = expenseService.findAllByUserId(userId);

        if (expenses == null || expenses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(expenses);
    }

    @Operation(summary = "Adiciona uma nova despesa")
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> add(@Valid @RequestBody ExpenseRequestDTO dto) {
        Expense expenseRequest = expenseService.add(dto);
        ExpenseResponseDTO expenseResponse = new ExpenseResponseDTO(expenseRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponse);
    }

}
