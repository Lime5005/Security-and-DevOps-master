package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testFindAllItemsHappyPath() {
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        itemList.add(item);
        when(itemRepository.findAll()).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Round Widget", Objects.requireNonNull(response.getBody()).get(0).getName());
    }

    @Test
    public void testFindItemByIdHappyPath() {
        Item item = new Item();
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Round Widget", Objects.requireNonNull(response.getBody()).getName());
    }

    @Test
    public void testFindItemByIdNegativePath() {
        Item item = new Item();
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        when(itemRepository.findById(0L)).thenReturn(Optional.of(item));

        final ResponseEntity<Item> response = itemController.getItemById(4L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindItemByNameHappyPath() {
        List<Item> itemList = new ArrayList<>();
        Item item = new Item();
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        itemList.add(item);
        when(itemRepository.findByName("Round Widget")).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Round Widget", Objects.requireNonNull(response.getBody()).get(0).getName());
    }

    @Test
    public void testFindItemByNameNegativePath() {
        List<Item> itemList = new ArrayList<Item>();
        Item item = new Item();
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        itemList.add(item);
        when(itemRepository.findByName("Round Widget")).thenReturn(itemList);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("SomeNoExisting Item");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }




}
