package com.kursinis.prif4kursinis.webControllers;

import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.CartItem;
import com.kursinis.prif4kursinis.model.Product;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartWeb {

//    private final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
//    private final CustomHib customHib = new CustomHib(entityManagerFactory);
//
//    @GetMapping("/active/{customerId}")
//    public Cart getActiveCart(@PathVariable int customerId) {
//        // Implement the logic to find the active cart for the customer
//        return customHib.findActiveCartByCustomerId(customerId);
//    }
//
//    @PostMapping("/add-item")
//    @ResponseStatus(HttpStatus.OK)
//    public void addItemToCart(@RequestParam int cartId, @RequestParam int productId, @RequestParam int quantity) {
//        // Implement the logic to add an item to the cart
//        Cart cart = customHib.findCartById(Cart.class, cartId);
//        Product product = customHib.find(Product.class, productId);
//        CartItem cartItem = new CartItem();
//        cartItem.setProduct(product);
//        cartItem.setQuantity(quantity);
//        // Add more logic as needed
//        customHib.saveOrUpdate(cartItem);
//    }
//
//    @PostMapping("/checkout/{cartId}")
//    public void checkoutCart(@PathVariable int cartId) {
//        Cart cart = customHib.findCartById(cartId);
//        cart.setStatus("Pending");
//        customHib.update(cart);
//    }
}