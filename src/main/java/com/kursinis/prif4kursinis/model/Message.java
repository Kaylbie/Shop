package com.kursinis.prif4kursinis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String messageBody;
    private LocalDateTime dateCreated;

    @ManyToOne
    private User sender; // The user who sent the message (could be a manager or a customer)

    @ManyToOne
    private Cart cart; // The cart to which this message is related

    public Message(User sender, String messageBody, Cart cart) {
        this.sender = sender;
        this.messageBody = messageBody;
        this.cart = cart;
        this.dateCreated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Message from " + sender.getName() + " on " + dateCreated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": " + messageBody;
    }
}
