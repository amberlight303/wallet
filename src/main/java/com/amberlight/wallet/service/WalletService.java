package com.amberlight.wallet.service;

import com.amberlight.wallet.model.domain.Wallet;
import com.amberlight.wallet.model.domain.Transaction;
import com.amberlight.wallet.model.dto.TransactionDto;
import com.amberlight.wallet.model.exception.BusinessLogicException;
import com.amberlight.wallet.repository.IWalletRepository;
import com.amberlight.wallet.repository.ITransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * An implementation of wallet services interface.
 */
@Service
@Slf4j(topic = "wallet")
public class WalletService implements IWalletService {

    @Autowired
    private IWalletRepository walletRepository;

    @Autowired
    private ITransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public Wallet getWalletByPlayerId(Integer playerId) {
        return walletRepository.findByPlayerId(playerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactionsByWalletId(Integer walletId) {
        return transactionRepository.findAllByWalletId(walletId);
    }

    @Override
    @Transactional
    public void debitWallet(TransactionDto transactionDto) {
        log.info("Debit transaction starts: {}", transactionDto);
        Wallet wallet = checkIfWalletExistsAndGet(transactionDto);
        checkIfTransactionDoesntExists(transactionDto);
        BigDecimal resultAmount = wallet.getBalance().subtract(transactionDto.getAmount());
        if (resultAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessLogicException("Insufficient funds.");
        }
        wallet.setBalance(resultAmount);
        walletRepository.save(wallet);
        log.info("New wallet state saved: {}", wallet);
        transactionDto.setAmount(transactionDto.getAmount().negate());
        saveTransaction(transactionDto, wallet);
        log.info("Debit transaction successfully finished");
    }

    @Override
    @Transactional
    public void creditWallet(TransactionDto transactionDto) {
        log.info("Credit transaction starts: {}", transactionDto);
        Wallet wallet = checkIfWalletExistsAndGet(transactionDto);
        checkIfTransactionDoesntExists(transactionDto);
        BigDecimal resultAmount = wallet.getBalance().add(transactionDto.getAmount());
        wallet.setBalance(resultAmount);
        walletRepository.save(wallet);
        log.info("New wallet state saved: {}", wallet);
        saveTransaction(transactionDto, wallet);
        log.info("Credit transaction successfully finished");
    }

    /**
     * Saves new transaction.
     * @param transactionDto transaction DTO
     * @param wallet current wallet state
     */
    private void saveTransaction(TransactionDto transactionDto, Wallet wallet) {
        Transaction transaction = new Transaction();
        transaction.setDate(new Date());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setId(transactionDto.getId());
        transaction.setWallet(wallet);
        transactionRepository.save(transaction);
        log.info("New transaction saved: {}", transaction);
    }

    /**
     * Checks if the wallet exists, returns the wallet.
     * @param transactionDto transaction DTO
     * @return wallet
     * @throws IllegalStateException in case if wallet doesn't exist
     */
    private Wallet checkIfWalletExistsAndGet(TransactionDto transactionDto) {
        Optional<Wallet> walletO = walletRepository.findById(transactionDto.getWalletId());
        if (walletO.isEmpty()) {
            throw new BusinessLogicException(String.format("A wallet with the id '%d' doesn't exist.",
                    transactionDto.getWalletId()));
        }
        return walletO.get();
    }

    /**
     * Checks if the transaction doesn't exist.
     * @param transactionDto transaction DTO
     * @throws IllegalStateException in case if the transaction already exists
     */
    private void checkIfTransactionDoesntExists(TransactionDto transactionDto) {
        Optional<Transaction> transactionO =  transactionRepository.findById(transactionDto.getId());
        if (transactionO.isPresent()) {
            throw new BusinessLogicException(String.format("A transaction with the id '%s' already exists.",
                    transactionDto.getId()));
        }
    }

}
