package org.bankinc.bankinc_api.service;

import org.springframework.stereotype.Service;
import org.bankinc.bankinc_api.entity.Transaction;
import org.bankinc.bankinc_api.repository.TransactionRepository;
import org.bankinc.bankinc_api.repository.CardRepository;
import org.bankinc.bankinc_api.entity.Card;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;

    public TransactionService(TransactionRepository transactionRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Transaction cancelTransaction(String cardNumber, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.getCardNumber().equals(cardNumber))
            throw new RuntimeException("Transaction does not belong to this card");

        if (transaction.isCancelled())
            throw new RuntimeException("Transaction is already cancelled");

        Duration duration = Duration.between(transaction.getDate(), LocalDateTime.now());
        if (duration.toHours() > 24)
            throw new RuntimeException("Cannot cancel transaction older than 24 hours");

        // Revertir saldo
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        card.setBalance(card.getBalance() + transaction.getPrice());
        cardRepository.save(card);

        transaction.setCancelled(true);
        return transactionRepository.save(transaction);
    }


}
