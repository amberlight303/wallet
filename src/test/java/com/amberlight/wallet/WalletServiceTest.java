package com.amberlight.wallet;


import com.amberlight.wallet.model.domain.Transaction;
import com.amberlight.wallet.model.domain.Wallet;
import com.amberlight.wallet.model.dto.TransactionDto;
import com.amberlight.wallet.model.exception.BusinessLogicException;
import com.amberlight.wallet.repository.ITransactionRepository;
import com.amberlight.wallet.repository.IWalletRepository;
import com.amberlight.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link com.amberlight.wallet.service.WalletService}
 */
@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private IWalletRepository walletRepository;

    @Mock
    private ITransactionRepository transactionRepository;

    @Spy
    @InjectMocks
    private WalletService walletService;

    @Test
    void getWalletByPlayerId_whenSuccess() {
        Integer playerId = 1;
        BigDecimal walletBalance = BigDecimal.valueOf(500);
        Wallet someWallet = new Wallet(playerId, null, walletBalance);
        when(walletRepository.findByPlayerId(playerId)).thenReturn(someWallet);
        Wallet wallet = walletService.getWalletByPlayerId(playerId);
        assertNotNull(wallet);
        assertEquals(playerId, wallet.getId());
        verify(walletRepository, times(1)).findByPlayerId(playerId);
    }

    @Test
    void getAllTransactionsByWalletId_whenSuccess() {
        Integer walletId = 1;
        String someTransactionId = "asd-asd-asd";
        BigDecimal transactionAmount = BigDecimal.valueOf(500);
        Transaction someTransaction = Transaction.builder().id(someTransactionId).date(new Date())
                                                           .amount(transactionAmount).build();
        when(transactionRepository.findAllByWalletId(walletId)).thenReturn(Collections.singletonList(someTransaction));
        List<Transaction> transactions = walletService.getAllTransactionsByWalletId(walletId);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        verify(transactionRepository, times(1)).findAllByWalletId(walletId);
    }

    @Test
    void debitWallet_whenWalletDoesntExist() {
        Integer walletId = 1;
        String newTransactionId = "asd-asd-asd";
        BigDecimal debitAmount = BigDecimal.valueOf(50);
        TransactionDto transactionDto = TransactionDto.builder().walletId(walletId).id(newTransactionId)
                                                      .amount(debitAmount).build();
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(null);
        Exception e = assertThrows(BusinessLogicException.class, () -> {
            walletService.debitWallet(transactionDto);
        });
        assertEquals(String.format("A wallet with the id '%d' doesn't exist.", transactionDto.getWalletId()),
                     e.getMessage());
    }

    @Test
    void debitWallet_whenTransactionWithTheSameIdAlreadyExists() {
        Integer walletId = 1;
        String existingTransactionId = "asd-asd-asd";
        BigDecimal debitAmount = BigDecimal.valueOf(50);
        BigDecimal walletBalance = BigDecimal.valueOf(100);
        BigDecimal existingTransactionAmount = BigDecimal.valueOf(30);
        TransactionDto debitTransactionDto = TransactionDto.builder().walletId(walletId).id(existingTransactionId)
                                                     .amount(debitAmount).build();
        Wallet existingWallet = Wallet.builder().id(walletId).balance(walletBalance).build();
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(existingWallet);
        Transaction existingTransaction =
                Transaction.builder().id(existingTransactionId).amount(existingTransactionAmount).build();
        when(transactionRepository.findById(debitTransactionDto.getId())).thenReturn(Optional.of(existingTransaction));
        Exception e = assertThrows(BusinessLogicException.class, () -> {
            walletService.debitWallet(debitTransactionDto);
        });
        assertEquals(String.format("A transaction with the id '%s' already exists.", debitTransactionDto.getId()),
                     e.getMessage());
    }

    @Test
    void debitWallet_whenInsufficientFunds() {
        Integer walletId = 1;
        String uniqueTransactionId = "asd-asd-asd";
        BigDecimal debitAmount = BigDecimal.valueOf(50);
        BigDecimal walletBalance = BigDecimal.valueOf(20);
        TransactionDto debitTransactionDto = TransactionDto.builder().walletId(walletId).id(uniqueTransactionId)
                                                     .amount(debitAmount).build();
        Wallet existingWallet = Wallet.builder().id(walletId).balance(walletBalance).build();
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(existingWallet);
        when(transactionRepository.findById(debitTransactionDto.getId())).thenReturn(Optional.empty());
        Exception e = assertThrows(BusinessLogicException.class, () -> {
            walletService.debitWallet(debitTransactionDto);
        });
        assertEquals("Insufficient funds.", e.getMessage());
    }

    @Test
    void debitWallet_whenSuccess() {
        // Preparing data
        Integer walletId = 1;
        String uniqueTransactionId = "asd-asd-asd";
        BigDecimal debitAmount = BigDecimal.valueOf(50);
        BigDecimal walletBalance = BigDecimal.valueOf(200);
        BigDecimal desiredWalletBalance = BigDecimal.valueOf(150);
        BigDecimal desiredTransactionAmount = BigDecimal.valueOf(-50);
        ArgumentCaptor<Transaction> transactionAgrCaptor = ArgumentCaptor.forClass(Transaction.class);
        TransactionDto debitTransactionDto = TransactionDto.builder().walletId(walletId).id(uniqueTransactionId)
                                                     .amount(debitAmount).build();
        Wallet existingWallet = Wallet.builder().id(walletId).balance(walletBalance).build();
        // When cases
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(existingWallet);
        when(transactionRepository.findById(debitTransactionDto.getId())).thenReturn(Optional.empty());
        // Method call
        walletService.debitWallet(debitTransactionDto);
        // Verifications
        assertEquals(desiredWalletBalance, existingWallet.getBalance());
        assertEquals(desiredTransactionAmount, debitTransactionDto.getAmount());
        verify(transactionRepository).save(transactionAgrCaptor.capture());
        Transaction savedTransaction = transactionAgrCaptor.getValue();
        assertEquals(debitTransactionDto.getId(), savedTransaction.getId());
        assertEquals(debitTransactionDto.getAmount(), savedTransaction.getAmount());
        assertNotNull(savedTransaction.getDate());
        assertEquals(existingWallet, savedTransaction.getWallet());
        verify(walletRepository, times(1)).findAndLockWalletById(debitTransactionDto.getWalletId());
        verify(transactionRepository, times(1)).findById(debitTransactionDto.getId());
        verify(walletRepository, times(1)).save(existingWallet);
        verify(transactionRepository, times(1)).save(any());
    }


    @Test
    void creditWallet_whenWalletDoesntExist() {
        Integer walletId = 1;
        String newTransactionId = "asd-asd-asd";
        BigDecimal creditAmount = BigDecimal.valueOf(50);
        TransactionDto transactionDto = TransactionDto.builder().walletId(walletId).id(newTransactionId)
                                                      .amount(creditAmount).build();
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(null);
        Exception e = assertThrows(BusinessLogicException.class, () -> {
            walletService.creditWallet(transactionDto);
        });
        assertEquals(String.format("A wallet with the id '%d' doesn't exist.", transactionDto.getWalletId()),
                     e.getMessage());
    }

    @Test
    void creditWallet_whenTransactionWithTheSameIdAlreadyExists() {
        Integer walletId = 1;
        String existingTransactionId = "asd-asd-asd";
        BigDecimal creditAmount = BigDecimal.valueOf(75);
        BigDecimal walletBalance = BigDecimal.valueOf(100);
        BigDecimal existingTransactionAmount = BigDecimal.valueOf(45);
        TransactionDto creditTransactionDto = TransactionDto.builder().walletId(walletId).id(existingTransactionId)
                                                     .amount(creditAmount).build();
        Wallet existingWallet = Wallet.builder().id(walletId).balance(walletBalance).build();
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(existingWallet);
        Transaction existingTransaction =
                Transaction.builder().id(existingTransactionId).amount(existingTransactionAmount).build();
        when(transactionRepository.findById(creditTransactionDto.getId())).thenReturn(Optional.of(existingTransaction));
        Exception e = assertThrows(BusinessLogicException.class, () -> {
            walletService.creditWallet(creditTransactionDto);
        });
        assertEquals(String.format("A transaction with the id '%s' already exists.", creditTransactionDto.getId()),
                     e.getMessage());
    }

    @Test
    void creditWallet_whenSuccess() {
        // Preparing data
        Integer walletId = 1;
        String uniqueTransactionId = "asd-asd-asd";
        BigDecimal creditAmount = BigDecimal.valueOf(75);
        BigDecimal walletBalance = BigDecimal.valueOf(100);
        BigDecimal desiredWalletBalance = BigDecimal.valueOf(175);
        BigDecimal desiredTransactionAmount = BigDecimal.valueOf(75);
        ArgumentCaptor<Transaction> transactionAgrCaptor = ArgumentCaptor.forClass(Transaction.class);
        TransactionDto creditTransactionDto = TransactionDto.builder().walletId(walletId).id(uniqueTransactionId)
                                                     .amount(creditAmount).build();
        Wallet existingWallet = Wallet.builder().id(walletId).balance(walletBalance).build();
        // When cases
        when(walletRepository.findAndLockWalletById(walletId)).thenReturn(existingWallet);
        when(transactionRepository.findById(creditTransactionDto.getId())).thenReturn(Optional.empty());
        // Method call
        walletService.creditWallet(creditTransactionDto);
        // Verifications
        assertEquals(desiredWalletBalance, existingWallet.getBalance());
        assertEquals(desiredTransactionAmount, creditTransactionDto.getAmount());
        verify(transactionRepository).save(transactionAgrCaptor.capture());
        Transaction savedTransaction = transactionAgrCaptor.getValue();
        assertEquals(creditTransactionDto.getId(), savedTransaction.getId());
        assertEquals(creditTransactionDto.getAmount(), savedTransaction.getAmount());
        assertNotNull(savedTransaction.getDate());
        assertEquals(existingWallet, savedTransaction.getWallet());
        verify(walletRepository, times(1)).findAndLockWalletById(creditTransactionDto.getWalletId());
        verify(transactionRepository, times(1)).findById(creditTransactionDto.getId());
        verify(walletRepository, times(1)).save(existingWallet);
        verify(transactionRepository, times(1)).save(any());
    }

}
