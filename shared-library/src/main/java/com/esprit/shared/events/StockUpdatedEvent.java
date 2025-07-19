package com.esprit.shared.events;

public record StockUpdatedEvent(
        String articleName,
        int oldQuantity,
        int newQuantity,
        String updateType // "INCREMENT" or "DECREMENT"
) {}

