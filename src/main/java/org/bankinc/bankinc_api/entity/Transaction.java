package org.bankinc.bankinc_api.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;
    private double price;
    private LocalDateTime date;

    private boolean cancelled = false; // nueva propiedad

    // Getters y Setters
    public Long getId() { return id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}