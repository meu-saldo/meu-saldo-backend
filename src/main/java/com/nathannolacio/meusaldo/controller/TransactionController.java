package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Cadastra uma nova transação")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação dos campos")
        }
    )
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO dto) {
        Transaction transactionRequest = transactionService.createTransaction(dto);
        TransactionResponseDTO transactionResponse = new TransactionResponseDTO(transactionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @Operation(summary = "Deleta uma transação pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transação deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "ID não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
