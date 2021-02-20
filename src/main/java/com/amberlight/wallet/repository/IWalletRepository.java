package com.amberlight.wallet.repository;

import com.amberlight.wallet.model.domain.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository for wallets.
 */
@Repository
public interface IWalletRepository extends CrudRepository<Wallet, Integer> {

    /**
     * Returns a wallet by a player id
     * @param playerId player id
     * @return wallet
     */
    Wallet findByPlayerId(Integer playerId);

}
