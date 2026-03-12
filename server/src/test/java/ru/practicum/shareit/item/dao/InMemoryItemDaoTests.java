package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryItemDaoTests {
    private InMemoryItemDao itemDao;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        itemDao = new InMemoryItemDao();

        item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Аккумуляторная дрель");
        item1.setAvailable(true);
        item1.setOwnerId(1L);

        item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Крестовая отвертка");
        item2.setAvailable(true);
        item2.setOwnerId(1L);
    }

    @Test
    void shouldAddItem() {
        Item addedItem = itemDao.addItem(item1);

        assertNotNull(addedItem.getId());
        assertEquals(1L, addedItem.getId());
        assertEquals("Дрель", addedItem.getName());
        assertEquals("Аккумуляторная дрель", addedItem.getDescription());
        assertTrue(addedItem.getAvailable());
        assertEquals(1L, addedItem.getOwnerId());
    }

    @Test
    void shouldThrowExceptionWhenAddNullItem() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemDao.addItem(null));

        assertEquals("Запрос на добавление вещи поступил с пустым телом", exception.getMessage());
    }

    @Test
    void shouldGetItemById() {
        Item addedItem = itemDao.addItem(item1);

        Item foundItem = itemDao.getItemById(addedItem.getId());

        assertNotNull(foundItem);
        assertEquals(addedItem.getId(), foundItem.getId());
        assertEquals(addedItem.getName(), foundItem.getName());
    }

    @Test
    void shouldThrowExceptionWhenItemNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemDao.getItemById(999L));

        assertEquals("Попытка получения вещи. Вещь с ID: 999 не найдена.", exception.getMessage());
    }

    @Test
    void shouldUpdateItemName() {
        Item addedItem = itemDao.addItem(item1);

        Item updatedItemData = new Item();
        updatedItemData.setName("Новая дрель");
        updatedItemData.setDescription(addedItem.getDescription());
        updatedItemData.setAvailable(addedItem.getAvailable());
        updatedItemData.setOwnerId(addedItem.getOwnerId());

        Item updatedItem = itemDao.updateItem(addedItem.getId(), updatedItemData);

        assertEquals("Новая дрель", updatedItem.getName());
        assertEquals(addedItem.getDescription(), updatedItem.getDescription());
        assertEquals(addedItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    @DisplayName("Должен успешно обновить описание вещи")
    void shouldUpdateItemDescription() {
        Item addedItem = itemDao.addItem(item1);

        Item updatedItemData = new Item();
        updatedItemData.setName(addedItem.getName());
        updatedItemData.setDescription("Новое описание");
        updatedItemData.setAvailable(addedItem.getAvailable());
        updatedItemData.setOwnerId(addedItem.getOwnerId());

        Item updatedItem = itemDao.updateItem(addedItem.getId(), updatedItemData);

        assertEquals("Новое описание", updatedItem.getDescription());
        assertEquals(addedItem.getName(), updatedItem.getName());
        assertEquals(addedItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void shouldUpdateItemAvailability() {
        Item addedItem = itemDao.addItem(item1);

        Item updatedItemData = new Item();
        updatedItemData.setName(addedItem.getName());
        updatedItemData.setDescription(addedItem.getDescription());
        updatedItemData.setAvailable(false);
        updatedItemData.setOwnerId(addedItem.getOwnerId());

        Item updatedItem = itemDao.updateItem(addedItem.getId(), updatedItemData);

        assertFalse(updatedItem.getAvailable());
        assertEquals(addedItem.getName(), updatedItem.getName());
        assertEquals(addedItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenUpdateNonExistingItem() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemDao.updateItem(999L, item1));

        assertEquals("Попытка обновления данных вещи. Вещь с ID: 999 не найдена.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithNullItem() {
        Item addedItem = itemDao.addItem(item1);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemDao.updateItem(addedItem.getId(), null));

        assertEquals("Запрос на обновление данных вещи поступил с пустым телом", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdateItemByNonOwner() {
        Item addedItem = itemDao.addItem(item1);

        Item updatedItemData = new Item();
        updatedItemData.setName("Новое имя");
        updatedItemData.setDescription(addedItem.getDescription());
        updatedItemData.setAvailable(addedItem.getAvailable());
        updatedItemData.setOwnerId(2L);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemDao.updateItem(addedItem.getId(), updatedItemData));

        assertEquals("Пользователь с ID: 2 не является владельцем вещи.", exception.getMessage());
    }

    @Test
    void shouldRemoveItem() {
        Item addedItem = itemDao.addItem(item1);

        Item removedItem = itemDao.removeItem(addedItem.getOwnerId(), addedItem.getId());

        assertNotNull(removedItem);
        assertEquals(addedItem.getId(), removedItem.getId());

        assertThrows(NotFoundException.class,
                () -> itemDao.getItemById(addedItem.getId()));
    }

    @Test
    void shouldThrowExceptionWhenRemoveNonExistingItem() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemDao.removeItem(1L, 999L));

        assertEquals("Попытка удаления пользователя. Пользователь с ID: 999 не найден", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRemoveItemByNonOwner() {
        Item addedItem = itemDao.addItem(item1);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemDao.removeItem(2L, addedItem.getId()));

        assertEquals("Пользователь с ID: 2 не является владельцем вещи.", exception.getMessage());
    }

    @Test
    void shouldGetAllItemsByOwner() {
        itemDao.addItem(item1);
        itemDao.addItem(item2);

        List<Item> userItems = itemDao.getAllItems(1L);

        assertEquals(2, userItems.size());
        assertTrue(userItems.stream().allMatch(item -> item.getOwnerId().equals(1L)));
    }

    @Test
    void shouldReturnEmptyListWhenNoItems() {
        List<Item> userItems = itemDao.getAllItems(1L);

        assertTrue(userItems.isEmpty());
    }

    @Test
    void shouldSearchItemsByText() {
        itemDao.addItem(item1);
        itemDao.addItem(item2);

        List<Item> searchResults = itemDao.searchItems("дрель");

        assertEquals(1, searchResults.size());
        assertEquals("Дрель", searchResults.get(0).getName());
    }

    @Test
    void shouldSearchItemsByDescription() {
        itemDao.addItem(item1);
        itemDao.addItem(item2);

        List<Item> searchResults = itemDao.searchItems("аккумуляторная");

        assertEquals(1, searchResults.size());
        assertEquals("Дрель", searchResults.get(0).getName());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsEmpty() {
        itemDao.addItem(item1);

        List<Item> searchResults = itemDao.searchItems("");

        assertTrue(searchResults.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenSearchTextIsNull() {
        itemDao.addItem(item1);

        List<Item> searchResults = itemDao.searchItems(null);

        assertTrue(searchResults.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoMatches() {
        itemDao.addItem(item1);

        List<Item> searchResults = itemDao.searchItems("несуществующий текст");

        assertTrue(searchResults.isEmpty());
    }

    @Test
    void shouldBeCaseInsensitive() {
        itemDao.addItem(item1);

        List<Item> searchResults = itemDao.searchItems("ДРЕЛЬ");

        assertEquals(1, searchResults.size());
        assertEquals("Дрель", searchResults.get(0).getName());
    }

    @Test
    void shouldFindOnlyAvailableItems() {
        Item unavailableItem = new Item();
        unavailableItem.setName("Пила");
        unavailableItem.setDescription("Электропила");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwnerId(1L);

        itemDao.addItem(item1);
        itemDao.addItem(unavailableItem);

        List<Item> searchResults = itemDao.searchItems("пила");

        assertTrue(searchResults.isEmpty());
    }

    @Test
    void shouldFindAvailableAndUnavailableItems() {
        Item unavailableItem = new Item();
        unavailableItem.setName("Пила");
        unavailableItem.setDescription("Электропила");
        unavailableItem.setAvailable(false);
        unavailableItem.setOwnerId(1L);

        Item availableItem = new Item();
        availableItem.setName("Пила");
        availableItem.setDescription("Ручная пила");
        availableItem.setAvailable(true);
        availableItem.setOwnerId(1L);

        itemDao.addItem(unavailableItem);
        itemDao.addItem(availableItem);

        List<Item> searchResults = itemDao.searchItems("пила");

        assertEquals(1, searchResults.size());
        assertTrue(searchResults.get(0).getAvailable());
        assertEquals("Ручная пила", searchResults.get(0).getDescription());
    }

    @Test
    void shouldCreateItemsWithUniqueIds() {
        Item item1 = itemDao.addItem(this.item1);
        Item item2 = itemDao.addItem(this.item2);

        assertNotEquals(item1.getId(), item2.getId());
        assertEquals(1L, item1.getId());
        assertEquals(2L, item2.getId());
    }

}