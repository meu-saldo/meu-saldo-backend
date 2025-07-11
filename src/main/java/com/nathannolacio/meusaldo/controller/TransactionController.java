package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.TransactionRequestDTO;
import com.nathannolacio.meusaldo.dto.TransactionResponseDTO;
import com.nathannolacio.meusaldo.model.Transaction;
import com.nathannolacio.meusaldo.security.CustomUserDetails;
import com.nathannolacio.meusaldo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Lista todas as transações do usuário logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações encontradas"),
            @ApiResponse(responseCode = "204", description = "Nenhuma transação encontrada")
    })
    @GetMapping
    @PreAuthorize("hasAny   Role('USER', 'ADMIN')")
    public ResponseEntity<List<TransactionResponseDTO>> getUserTransactions(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        Long userId = user.getId();

        List<TransactionResponseDTO> transactions = transactionService.findAllByUserId(userId);

        if (transactions == null || transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Lista todas as transações de todos os usuários")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transações encontradas"),
            @ApiResponse(responseCode = "204", description = "Nenhuma transação encontrada")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<TransactionResponseDTO>> findAll() {
        List<TransactionResponseDTO> transactions = transactionService.findAll();

        if (transactions == null || transactions.isEmpty()) {
            return ResponseEntity.noContent().build(); // Retorna 204
        }

        return ResponseEntity.ok(transactions);
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
