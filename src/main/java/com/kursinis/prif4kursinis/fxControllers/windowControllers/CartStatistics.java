package com.kursinis.prif4kursinis.fxControllers.windowControllers;

import com.kursinis.prif4kursinis.model.Cart;

import java.util.List;

public class CartStatistics {
    private int pendingCount;
    private int openCount;
    private int closedCount;

    public CartStatistics(List<Cart> carts) {
        for (Cart cart : carts) {
            switch (cart.getStatus()) {
                case "Pending":
                    pendingCount++;
                    break;
                case "Open":
                    openCount++;
                    break;
                case "Closed":
                    closedCount++;
                    break;
            }
        }
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public int getOpenCount() {
        return openCount;
    }

    public int getClosedCount() {
        return closedCount;
    }
}
