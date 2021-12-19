package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmitOrderHappyPath() {
        Item item = createItem(8L, "Rectangle", BigDecimal.valueOf(7.99), "A Rectangle to play with");
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createCart(8L, items, null);
        User user = createUser(8L, "Lily", "liLy*Nice", cart);
        cart.setUser(user);

        when(userRepository.findByUsername("Lily")).thenReturn(user);

        final ResponseEntity<UserOrder> response = orderController.submit("Lily");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assert order != null;
        assertEquals(user, order.getUser());
        assertEquals(items, order.getItems());
        assertEquals(BigDecimal.valueOf(7.99), order.getTotal());
    }


    @Test
    public void testSubmitOrderNegativePath() {
        when(userRepository.findByUsername("John")).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit("John");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrderForUserHappyPath() {
        Item item = createItem(7L, "Hexagon", BigDecimal.valueOf(9.99), "An Hexagon to play with.");
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createCart(7L, new ArrayList<>(), null);
        User user = createUser(7L, "John", "James*755", null);
        cart.setUser(user);
        cart.setItems(items);
        user.setCart(cart);

        orderController.submit("John");
        when(userRepository.findByUsername("John")).thenReturn(user);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("John");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void testGetOrderForUserNegativePath() {
        when(userRepository.findByUsername("James")).thenReturn(null);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("James");
        assertEquals(404, response.getStatusCodeValue());
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
        newCart.setTotal(BigDecimal.valueOf(7.99));
        return newCart;
    }

}
