package com.nathannolacio.meusaldo.controller;

import com.nathannolacio.meusaldo.dto.AccountRequestDTO;
import com.nathannolacio.meusaldo.dto.AccountResponseDTO;
import com.nathannolacio.meusaldo.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @Operation(summary = "Cadastra uma nova conta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Conta com nome já cadastrada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO dto) {
        AccountResponseDTO accountResponse = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponse);
    }

    @Operation(summary = "Lista todas as contas cadastradas do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuários retornados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Operação realizada com sucesso, porém com retorno vazio"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accounts = accountService.getAllAccounts();

        if (accounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Deleta uma conta pelo Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Conta removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateAccount(@PathVariable Long id) {
        accountService.deactivateAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Edita os dados de uma conta do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta editada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada"),
            @ApiResponse(responseCode = "409", description = "Nome de conta já utilizado por esse usuário"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> editAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO dto) {
        AccountResponseDTO accountUpdated = accountService.editAccount(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(accountUpdated);
    }

}
