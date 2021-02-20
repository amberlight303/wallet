package com.amberlight.wallet.web.controller;

import com.amberlight.wallet.model.domain.Wallet;
import com.amberlight.wallet.model.domain.Transaction;
import com.amberlight.wallet.model.dto.TransactionDto;
import com.amberlight.wallet.service.IWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * A REST controller for operations related to wallets.
 */
@RestController
@RequestMapping("/wallet")
@Slf4j(topic = "wallet")
public class WalletController {

    @Autowired
    private IWalletService walletService;

    /**
     * Returns a wallet state by a player id.
     * @param playerId player id
     * @return wallet
     */
    @GetMapping("/state")
    public Wallet getWalletState(@RequestParam("playerId") Integer playerId) {
        return walletService.getWalletByPlayerId(playerId);
    }

    /**
     * Returns all transactions by a wallet id.
     * @param walletId wallet id
     * @return transactions
     */
    @GetMapping("/transactions")
    public List<Transaction> getTransactions(@RequestParam("walletId") Integer walletId) {
        return walletService.getAllTransactionsByWalletId(walletId);
    }

    /**
     * Withdraws funds from the wallet.
     * @param transactionDto transaction DTO
     */
    @PostMapping("/debit")
    public void debitWallet(@RequestBody @Valid TransactionDto transactionDto) {
        walletService.debitWallet(transactionDto);
    }

    /**
     * Adds funds to the wallet.
     * @param transactionDto transaction DTO
     */
    @PostMapping("/credit")
    public void creditWallet(@RequestBody @Valid TransactionDto transactionDto) {
        walletService.creditWallet(transactionDto);
    }

}
