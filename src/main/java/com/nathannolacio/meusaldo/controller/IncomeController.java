package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.IncomeResponseDTO;
import com.nathannolacio.meusaldo.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/incomes")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @Operation(summary = "Lista todas as entradas cadastradas pelo usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @GetMapping
    public ResponseEntity<List<IncomeResponseDTO>> getUserIncomes() {
        List<IncomeResponseDTO> incomes = incomeService.getUserIncomes();

        if (incomes == null || incomes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(incomes);
    }

}
