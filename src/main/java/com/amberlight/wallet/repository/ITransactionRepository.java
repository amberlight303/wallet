package com.amberlight.wallet.repository;

import com.amberlight.wallet.model.domain.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository for transactions.
 */
@Repository
public interface ITransactionRepository extends CrudRepository<Transaction, String> {

    /**
     * Returns all transactions by a wallet id.
     * @param walletId wallet id
     * @return transactions
     */
    List<Transaction> findAllByWalletId(Integer walletId);

}
