package com.amberlight.wallet.repository;


import com.amberlight.wallet.model.domain.Wallet;

/**
 * A custom repository for wallets.
 */
public interface IWalletRepositoryCustom {

    /**
     * Finds, locks and returns a wallet by its id.
     * Use it only in the transaction scope.
     * @param id wallet's id
     * @return wallet
     */
    Wallet findAndLockWalletById(Integer id);

}
