package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {

    // Добавляем новую вещь.
    Item addItem(Item item);

    // Получаем вещь по ID.
    Item getItemById(Long itemId);

    // Обновляем данные имеющейся вещи.
    Item updateItem(Long id, Item updatedItem);

    // Удаляем имеющуюся вещь.
    Item removeItem(Long ownerId, Long itemId);

    // Получаем список всех имеющихся вещей.
    List<Item> getAllItems(Long ownerId);

    // Поиск вещей по содержанию определенного текста в названии или описании.
    List<Item> searchItems(String text);

}