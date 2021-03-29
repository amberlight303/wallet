package com.amberlight.wallet.repository;


import com.amberlight.wallet.model.domain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

/**
 * An implementation of a custom repository for wallets.
 */
@Repository
public class WalletRepositoryCustom implements IWalletRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public Wallet findAndLockWalletById(Integer id) {
        return entityManager.find(Wallet.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

}
