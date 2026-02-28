package org.bankinc.bankinc_api.service;

import org.bankinc.bankinc_api.entity.Card;
import org.bankinc.bankinc_api.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.bankinc.bankinc_api.repository.TransactionRepository;
import org.bankinc.bankinc_api.entity.Transaction;
import java.time.LocalDate;
import java.util.Random;
import java.time.LocalDateTime;  //  LocalDateTime en purchase

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public CardService(CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }



    // Generar número de tarjeta
    public Card generateCard(String productId, String holderName) {
        // Validar productId
        if (!productId.matches("\\d{6}")) {
            throw new RuntimeException("ProductId must be exactly 6 digits");
        }
        String cardNumber = generateCardNumber(productId);

        Card card = new Card();
        card.setProductId(productId);
        card.setCardNumber(cardNumber);
        card.setHolderName(holderName);
        card.setExpirationDate(LocalDate.now().plusYears(3));
        card.setBalance(0);
        card.setActive(false);
        card.setBlocked(false);

        return cardRepository.save(card);
    }

    private String generateCardNumber(String productId) {

        Random random = new Random();
        StringBuilder sb = new StringBuilder(productId);

        while (sb.length() < 16) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

    //activar tarjeta
    public Card enrollCard(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (card.isActive()) {
            throw new RuntimeException("Card is already active");
        }

        if (card.isBlocked()) {
            throw new RuntimeException("Blocked card cannot be activated");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Card is expired");
        }

        card.setActive(true);
        return cardRepository.save(card);
    }
    //Bloquear tarjeta
    public Card blockCard(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (card.isBlocked()) {
            throw new RuntimeException("Card is already blocked");
        }

        if (!card.isActive()) {
            throw new RuntimeException("Inactive card cannot be blocked");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expired card cannot be blocked");
        }
        card.setBlocked(true);
        return cardRepository.save(card);
    }
    // Recargar saldo
    public Card rechargeBalance(String cardNumber, double amount) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (amount <= 0) {
            throw new RuntimeException("Recharge amount must be greater than zero");
        }

        if (!card.isActive()) {
            throw new RuntimeException("Inactive card cannot be recharged");
        }

        if (card.isBlocked()) {
            throw new RuntimeException("Blocked card cannot be recharged");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expired card cannot be recharged");
        }

        // balance puede ser null, así que usamos Double
        Double currentBalance = card.getBalance();
        if(currentBalance == null) {
            currentBalance = 0.0;
        }

        card.setBalance(currentBalance + amount); // suma segura Double + double
        return cardRepository.save(card);
    }

    // Obtener saldo de la tarjeta
    public Double getBalance(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            throw new RuntimeException("Card number is required");
        }
        if (!card.isActive()) {
            throw new RuntimeException("Inactive card cannot check balance");
        }

        if (card.isBlocked()) {
            throw new RuntimeException("Blocked card cannot check balance");
        }

        if (card.getExpirationDate() != null &&
                card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expired card cannot check balance");
        }

        // Si balance es null, devolvemos 0.0
        Double currentBalance = card.getBalance();
        if (currentBalance == null) {
            currentBalance = 0.0;
        }

        return currentBalance;
    }
    //transaccion de compra
    public Card purchaseCard(String cardNumber, double price) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        // Balance primitivo, nunca null
        double currentBalance = card.getBalance();

        // Verificar si la tarjeta está bloqueada
        if (card.isBlocked()) {
            throw new RuntimeException("Cannot perform purchase: card is blocked");
        }
        if (!card.isActive()) {
            throw new RuntimeException("Inactive card cannot perform purchases");
        }
        if (price <= 0) {
            throw new RuntimeException("Price must be greater than zero");
        }
        if (card.getExpirationDate() != null &&
                card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Expired card cannot perform purchases");
        }

        // Verificar saldo suficiente
        if (currentBalance < price) {
            throw new RuntimeException("Insufficient balance");
        }

        // Descontar el precio
        card.setBalance(currentBalance - price);
        // Crear transacción
        Transaction transaction = new Transaction();
        transaction.setCardNumber(cardNumber);
        transaction.setPrice(price);
        transaction.setDate(LocalDateTime.now());
        transactionRepository.save(transaction);
        // Guardar cambios
        return cardRepository.save(card);
    }


    // Consultar transacción por ID
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }



}