package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.IncomeRequestDTO;
import com.nathannolacio.meusaldo.dto.IncomeResponseDTO;
import com.nathannolacio.meusaldo.model.Income;
import com.nathannolacio.meusaldo.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Adiciona uma nova Entrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "409", description = "Entrada já cadastrada")
    })
    @PostMapping
    public ResponseEntity<IncomeResponseDTO> add(@Valid @RequestBody IncomeRequestDTO dto) {
        IncomeResponseDTO income = incomeService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(income);
    }

    @Operation(summary = "Exclui uma despesa pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Entrada não encontrada"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        incomeService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Edita os dados de uma entrada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Entrada não encontrada"),
            @ApiResponse(responseCode = "409", description = "Entrada já existe")
    })
    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponseDTO> edit(@PathVariable Long id, @Valid @RequestBody IncomeRequestDTO dto) {
        IncomeResponseDTO incomeResponse = incomeService.edit(id, dto);
        return ResponseEntity.ok(incomeResponse);
    }

}
