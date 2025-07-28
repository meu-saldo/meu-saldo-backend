package com.nathannolacio.meusaldo.service;

import com.nathannolacio.meusaldo.dto.IncomeRequestDTO;
import com.nathannolacio.meusaldo.dto.IncomeResponseDTO;
import com.nathannolacio.meusaldo.exception.IncomeAlreadyExistsException;
import com.nathannolacio.meusaldo.exception.IncomeNotFoundException;
import com.nathannolacio.meusaldo.model.Income;
import com.nathannolacio.meusaldo.model.User;
import com.nathannolacio.meusaldo.repository.IncomeRepository;
import com.nathannolacio.meusaldo.util.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomeServiceTest {

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private AuthUtils authUtils;

    @InjectMocks
    private IncomeService incomeService;

    private User user;
    private Income income;
    private IncomeRequestDTO incomeRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("testuser");

        income = new Income("Salário", new BigDecimal("5000.00"), user);
        income.setId(10L);

        incomeRequestDTO = new IncomeRequestDTO("Salário", new BigDecimal("5000.00"));
    }

    @Test
    @DisplayName("'getUserIncomes' deve retornar uma lista de 'IncomeResponseDTO' para usuários autenticados")
    void getUserIncomes_ShouldReturnListOfDTOs() {
        when(authUtils.getAuthenticatedUserId()).thenReturn(user.getId());
        when(incomeRepository.findByUserId(user.getId())).thenReturn(List.of(income));

        List<IncomeResponseDTO> result = incomeService.getUserIncomes();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().description()).isEqualTo(income.getDescription());
        assertThat(result.getFirst().amount()).isEqualTo(income.getAmount());

        verify(authUtils).getAuthenticatedUserId();
        verify(incomeRepository).findByUserId(user.getId());
    }

    @Test
    @DisplayName("'add' deve salvar uma nova entrada quando a descrição for única")
    void add_WhenDescriptionIsUnique_ShouldSaveAndReturnDTO() {
        when(authUtils.getAuthenticatedUser()).thenReturn(user);
        when(incomeRepository.existsByDescriptionAndUserId(incomeRequestDTO.description(), user.getId())).thenReturn(false);
        when(incomeRepository.save(any(Income.class))).thenReturn(income);

        IncomeResponseDTO result = incomeService.add(incomeRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(incomeRequestDTO.description());
        assertThat(result.amount()).isEqualTo(incomeRequestDTO.amount());

        ArgumentCaptor<Income> incomeArgumentCaptor = ArgumentCaptor.forClass(Income.class);
        verify(incomeRepository).save(incomeArgumentCaptor.capture());

        Income savedIncome = incomeArgumentCaptor.getValue();
        assertThat(savedIncome.getUser()).isEqualTo(user);
        assertThat(savedIncome.getDescription()).isEqualTo(incomeRequestDTO.description());
    }

    @Test
    @DisplayName("'add' deve lançar uma 'IncomeAlreadyExistsException' quando a descrição já existir")
    void add_WhenDescriptionExists_ShouldThrowException() {
        when(authUtils.getAuthenticatedUser()).thenReturn(user);
        when(incomeRepository.existsByDescriptionAndUserId(incomeRequestDTO.description(), user.getId())).thenReturn(true);

        assertThrows(IncomeAlreadyExistsException.class, () -> {
            incomeService.add(incomeRequestDTO);
        });

        verify(incomeRepository, never()).save(any(Income.class));
    }

    @Test
    @DisplayName("'delete' deve remover a entrada se ela existir e pertencer ao usuário0")
    void delete_WhenIncomeExists_ShouldCallDelete() {
        when(authUtils.getAuthenticatedUserId()).thenReturn(user.getId());
        when(incomeRepository.findByIdAndUserId(income.getId(), user.getId())).thenReturn(Optional.of(income));

        incomeService.delete(income.getId());

        verify(incomeRepository).delete(income);
    }

    @Test
    @DisplayName("'delete' deve lançar uma 'IncomeNotFoundException' quando a entrada não exisitir")
    void delete_WhenIncomeNotFound_ShouldThrowException() {
        Long nonExistentId = 99L;
        when(authUtils.getAuthenticatedUserId()).thenReturn(user.getId());
        when(incomeRepository.findByIdAndUserId(nonExistentId, user.getId())).thenReturn(Optional.empty());

        assertThrows(IncomeNotFoundException.class, () -> {
            incomeService.delete(nonExistentId);
        });

        verify(incomeRepository, never()).delete(any());
    }

    @Test
    @DisplayName("'edit' deve atualizar uma entrada se for encontrada e a descrição não for duplicada")
    void edit_WhenIncomeFoundAndDescriptionIsUnique_ShouldUpdateAndReturnDTO() {
        IncomeRequestDTO updatedDto = new IncomeRequestDTO("Bônus Anual", new BigDecimal("10000.00"));

        when(authUtils.getAuthenticatedUserId()).thenReturn(user.getId());
        when(incomeRepository.findByIdAndUserId(income.getId(), user.getId())).thenReturn(Optional.of(income));
        when(incomeRepository.existsByDescriptionAndUserIdAndIdNot(updatedDto.description(), user.getId(), income.getId())).thenReturn(false);

        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IncomeResponseDTO result = incomeService.edit(income.getId(), updatedDto);

        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo(updatedDto.description());
        assertThat(result.amount()).isEqualTo(updatedDto.amount());

        verify(incomeRepository).save(income);
    }

    @Test
    @DisplayName("'edit' deve lançar uma 'IncomeAlreadyExistsException' quando a descrição for duplicada")
    void edit_WhenNewDescriptionIsDuplicate_ShouldThrowException() {
        IncomeRequestDTO updatedDto = new IncomeRequestDTO("Outro Salário", new BigDecimal("1000.00"));

        when(authUtils.getAuthenticatedUserId()).thenReturn(user.getId());
        when(incomeRepository.findByIdAndUserId(income.getId(), user.getId())).thenReturn(Optional.of(income));

        when(incomeRepository.existsByDescriptionAndUserIdAndIdNot(updatedDto.description(), user.getId(), income.getId())).thenReturn(true);

        assertThrows(IncomeAlreadyExistsException.class, () -> {
            incomeService.edit(income.getId(), updatedDto);
        });

        verify(incomeRepository, never()).save(any());
    }

    @Test
    @DisplayName("'edit' não deve validar a descrição se ela não for alterada")
    void edit_WhenDescriptionHasNotChanged_ShouldNotValidateDuplicate() {
        IncomeRequestDTO updatedDto = new IncomeRequestDTO(income.getDescription(), new BigDecimal("5500.00"));

        when(authUtils.getAuthenticatedUserId()).thenReturn(user.getId());
        when(incomeRepository.findByIdAndUserId(income.getId(), user.getId())).thenReturn(Optional.of(income));
        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));

        incomeService.edit(income.getId(), updatedDto);

        verify(incomeRepository, never()).existsByDescriptionAndUserIdAndIdNot(anyString(), anyLong(), anyLong());
        verify(incomeRepository).save(income); // Mas o save ainda deve ser chamado
    }
}