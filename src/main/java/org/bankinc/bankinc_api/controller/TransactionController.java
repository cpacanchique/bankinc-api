package org.bankinc.bankinc_api.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.bankinc.bankinc_api.entity.Transaction;
import org.bankinc.bankinc_api.service.TransactionService;
import org.springframework.http.ResponseEntity;
import java.util.Map;
@RestController
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long transactionId) {
        Transaction transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/anulation")
    public ResponseEntity<Transaction> cancelTransaction(@RequestBody Map<String, String> body) {
        String cardId = body.get("cardId");
        Long transactionId = Long.parseLong(body.get("transactionId"));
        Transaction cancelled = transactionService.cancelTransaction(cardId, transactionId);
        return ResponseEntity.ok(cancelled);
    }
}