package org.bankinc.bankinc_api.controller;

import org.bankinc.bankinc_api.entity.Card;
import org.bankinc.bankinc_api.service.CardService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.bankinc.bankinc_api.entity.Transaction;

@RestController
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;
    private Double balance = 0.0; // inicializado por defecto


    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{productId}/number")
    public Card generateCard(
            @PathVariable String productId,
            @RequestParam String holderName) {

        return cardService.generateCard(productId, holderName);
    }

    /**
     * 2. Activar tarjeta
     * POST /card/enroll
     */
    @PostMapping("/enroll")
    public ResponseEntity<Card> enroll(@RequestBody Map<String, String> body) {
        String cardNumber = body.get("cardId");
        return ResponseEntity.ok(cardService.enrollCard(cardNumber));
    }

    /**
     * 3. Bloquear tarjeta
     * POST DELETE /card/{cardId}
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Card> blockCard(@PathVariable String cardId) {
        return ResponseEntity.ok(cardService.blockCard(cardId));
    }

    /**
     * 4. Recargar saldo
     * /card/balance
     */
    @PostMapping("/balance")
    public ResponseEntity<Card> rechargeBalance(@RequestBody Map<String, String> body) {
        String cardNumber = body.get("cardId");
        double amount = Double.parseDouble(body.get("balance"));
        Card updatedCard = cardService.rechargeBalance(cardNumber, amount);
        return ResponseEntity.ok(updatedCard);
    }

    // 5.Endpoint para consultar saldo
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<Double> getBalance(@PathVariable String cardId) {
        Double balance = cardService.getBalance(cardId);
        return ResponseEntity.ok(balance);
    }

    // transaccion de compra
    @PostMapping("/transaction/purchase")
    public ResponseEntity<Card> purchase(@RequestBody Map<String, Object> body) {
        String cardId = (String) body.get("cardId");
        double price = Double.parseDouble(body.get("price").toString());

        Card updatedCard = cardService.purchaseCard(cardId, price);
        return ResponseEntity.ok(updatedCard);
    }

    // Consultar transacción de compra
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        Transaction transaction = cardService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }


}