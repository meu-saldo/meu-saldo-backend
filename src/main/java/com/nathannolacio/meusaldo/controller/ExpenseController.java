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
    public ResponseEntity<List<ExpenseResponseDTO>> getUserExpenses() {
        List<ExpenseResponseDTO> expenses = expenseService.getUserExpenses();

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

    @Operation(summary = "Exclui uma despesa pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Edita os dados de uma despesa")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> edit(@Valid @RequestBody ExpenseRequestDTO dto,
                                                   @PathVariable Long id) {
        Expense edited = expenseService.edit(id, dto);
        ExpenseResponseDTO responseDTO = new ExpenseResponseDTO(edited);
        return ResponseEntity.ok(responseDTO);
    }

}
