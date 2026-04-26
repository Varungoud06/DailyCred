package com.unqiuehire.kashflow.serviceimpl;

import com.unqiuehire.kashflow.constant.WalletOwnerType;
import com.unqiuehire.kashflow.constant.WalletTransactionType;
import com.unqiuehire.kashflow.entity.Wallet;
import com.unqiuehire.kashflow.entity.WalletTransaction;
import com.unqiuehire.kashflow.repository.WalletRepository;
import com.unqiuehire.kashflow.repository.WalletTransactionRepository;
import com.unqiuehire.kashflow.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public Wallet getOrCreateWallet(WalletOwnerType walletOwnerType, Long ownerId) {
        return walletRepository.findByWalletOwnerTypeAndOwnerId(walletOwnerType, ownerId)
                .orElseGet(() -> {
                    Wallet wallet = new Wallet();
                    wallet.setWalletOwnerType(walletOwnerType);
                    wallet.setOwnerId(ownerId);
                    wallet.setBalance(BigDecimal.ZERO);
                    wallet.setActive(true);
                    return walletRepository.save(wallet);
                });
    }

    @Override
    public void creditWallet(
            WalletOwnerType walletOwnerType,
            Long ownerId,
            BigDecimal amount,
            WalletTransactionType transactionType,
            String description,
            Long loanId,
            Long repaymentId
    ) {
        Wallet wallet = getOrCreateWallet(walletOwnerType, ownerId);
        BigDecimal before = wallet.getBalance();
        BigDecimal after = before.add(amount);

        wallet.setBalance(after);
        walletRepository.save(wallet);

        WalletTransaction txn = new WalletTransaction();
        txn.setWalletId(wallet.getWalletId());
        txn.setAmount(amount);
        txn.setTransactionType(transactionType);
        txn.setDescription(description);
        txn.setTransactionTime(LocalDateTime.now());
        txn.setBalanceBefore(before);
        txn.setBalanceAfter(after);
        txn.setLoanId(loanId);
        txn.setRepaymentId(repaymentId);

        walletTransactionRepository.save(txn);
    }

    @Override
    public void debitWallet(
            WalletOwnerType walletOwnerType,
            Long ownerId,
            BigDecimal amount,
            WalletTransactionType transactionType,
            String description,
            Long loanId,
            Long repaymentId
    ) {
        Wallet wallet = getOrCreateWallet(walletOwnerType, ownerId);
        BigDecimal before = wallet.getBalance();
        BigDecimal after = before.subtract(amount);

        wallet.setBalance(after);
        walletRepository.save(wallet);

        WalletTransaction txn = new WalletTransaction();
        txn.setWalletId(wallet.getWalletId());
        txn.setAmount(amount);
        txn.setTransactionType(transactionType);
        txn.setDescription(description);
        txn.setTransactionTime(LocalDateTime.now());
        txn.setBalanceBefore(before);
        txn.setBalanceAfter(after);
        txn.setLoanId(loanId);
        txn.setRepaymentId(repaymentId);

        walletTransactionRepository.save(txn);
    }
}