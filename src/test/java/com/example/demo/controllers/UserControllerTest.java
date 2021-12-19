package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void init() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }


    @Test
    public void testCreateUserHappyPath() {
        when(bCryptPasswordEncoder.encode("somePassword")).thenReturn("someHashedPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("somePassword");
        request.setConfirmPassword("somePassword");
        final ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("someHashedPassword", user.getPassword());

    }

    @Test
    public void testCreateUserNegativePath() {
        when(bCryptPasswordEncoder.encode("pass")).thenReturn("someHashedPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("pass");
        request.setConfirmPassword("pass");

        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUserNameHappyPath() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("somePassword");
        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test", Objects.requireNonNull(response.getBody()).getUsername());
        assertEquals("somePassword", response.getBody().getPassword());
    }


    @Test
    public void testFindByUserNameNegativePath() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("somePassword");
        when(userRepository.findByUsername("test")).thenReturn(user);

        final ResponseEntity<User> response = userController.findByUserName("someNoExistingUserName");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindByIdHappyPath() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("somePassword");
        when(userRepository.findById(0L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test", Objects.requireNonNull(response.getBody()).getUsername());
        assertEquals("somePassword", response.getBody().getPassword());
    }

    @Test
    public void testFindByIdNegativePath() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("somePassword");
        when(userRepository.findById(0L)).thenReturn(Optional.of(user));

        final ResponseEntity<User> response = userController.findById(2L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

}
