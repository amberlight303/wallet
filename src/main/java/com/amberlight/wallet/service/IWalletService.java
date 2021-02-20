package com.amberlight.wallet.service;

import com.amberlight.wallet.model.domain.Wallet;
import com.amberlight.wallet.model.domain.Transaction;
import com.amberlight.wallet.model.dto.TransactionDto;

import java.util.List;

/**
 * A wallet services interface.
 */
public interface IWalletService {

    /**
     * Returns a wallet by a player id.
     * @param playerId player id
     * @return wallet
     */
    Wallet getWalletByPlayerId(Integer playerId);

    /**
     * Returns all transactions by a wallet id.
     * @param walletId wallet id
     * @return transactions
     */
    List<Transaction> getAllTransactionsByWalletId(Integer walletId);

    /**
     * Withdraws funds from the wallet.
     * @param transactionDto transaction DTO
     */
    void debitWallet(TransactionDto transactionDto);

    /**
     * Adds funds to the wallet.
     * @param transactionDto transaction DTO
     */
    void creditWallet(TransactionDto transactionDto);

}
