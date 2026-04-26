package com.unqiuehire.kashflow.service;

import com.unqiuehire.kashflow.constant.WalletOwnerType;
import com.unqiuehire.kashflow.constant.WalletTransactionType;
import com.unqiuehire.kashflow.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    Wallet getOrCreateWallet(WalletOwnerType walletOwnerType, Long ownerId);

    void creditWallet(
            WalletOwnerType walletOwnerType,
            Long ownerId,
            BigDecimal amount,
            WalletTransactionType transactionType,
            String description,
            Long loanId,
            Long repaymentId
    );

    void debitWallet(
            WalletOwnerType walletOwnerType,
            Long ownerId,
            BigDecimal amount,
            WalletTransactionType transactionType,
            String description,
            Long loanId,
            Long repaymentId
    );
}