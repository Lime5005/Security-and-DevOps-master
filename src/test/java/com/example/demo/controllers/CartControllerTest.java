package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCartHappyPath() {
        ModifyCartRequest newCartRequest = createCartRelatedInfo();

        final ResponseEntity<Cart> response = cartController.addTocart(newCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(2, cart.getItems().size());
    }

    @Test
    public void testRemoveFromCartHappyPath() {
        ModifyCartRequest newCartRequest = createCartRelatedInfo();

        final ResponseEntity<Cart> response = cartController.removeFromcart(newCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void testAddToCartNoUserNegativePath() {
        ModifyCartRequest newCartRequest = createCartRequest(1L, 10, "John");

        when(userRepository.findByUsername("John")).thenReturn(null);
        when(itemRepository.findById(anyLong())).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(newCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartNoItemNegativePath() {
        Cart newCart = new Cart();
        User newUser = createUser(6L, "John", "James*777", newCart);
        ModifyCartRequest newCartRequest = createCartRequest(6L, 3, "John");

        when(userRepository.findByUsername("John")).thenReturn(newUser);
        when(itemRepository.findById(6L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.addTocart(newCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartNoUserNegativePath() {
        ModifyCartRequest newCartRequest = createCartRequest(1L, 3, "John");

        when(userRepository.findByUsername("John")).thenReturn(null);
        final ResponseEntity<Cart> response = cartController.removeFromcart(newCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartNoItemNegativePath() {
        Cart newCart = new Cart();
        User newUser = createUser(3L, "John", "james*777", newCart);
        ModifyCartRequest newCartRequest = createCartRequest(3L, 4, "John");

        when(userRepository.findByUsername("John")).thenReturn(newUser);
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());

        final ResponseEntity<Cart> response = cartController.removeFromcart(newCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    public ModifyCartRequest createCartRelatedInfo() {
        Cart newCart = new Cart();
        User newUser = createUser(5L, "John", "James*777", newCart);
        Item newItem = createItem(5L, "Rectangle", new BigDecimal("2.99"), "A rectangle to play with");
        ModifyCartRequest newCartRequest = createCartRequest(5L, 2, "John");
        ArrayList<Item> listOfItems = new ArrayList<>();
        listOfItems.add(newItem);
        createCart(5L, listOfItems, newUser);

        when(userRepository.findByUsername("John")).thenReturn(newUser);
        when(itemRepository.findById(5L)).thenReturn(Optional.of(newItem));
        return newCartRequest;
    }

    public ModifyCartRequest createCartRequest(long itemId, int quantity, String username) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setQuantity(quantity);
        cartRequest.setUsername(username);
        return cartRequest;
    }

    public User createUser(long userId, String username, String password, Cart cart) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setCart(cart);
        return newUser;
    }

    public Item createItem(Long id, String name, BigDecimal price, String description) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setPrice(price);
        newItem.setDescription(description);
        return newItem;
    }

    public Cart createCart(long cartId, ArrayList<Item> items, User user) {
        Cart newCart = new Cart();
        newCart.setId(cartId);
        newCart.setItems(items);
        newCart.setUser(user);
        return newCart;
    }



}
