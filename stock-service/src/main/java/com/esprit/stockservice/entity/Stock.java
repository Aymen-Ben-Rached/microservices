package com.esprit.stockservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("stocks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Stock {
    @Id
    private String id;
    private String articleName;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
