package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    // Добавляем новую вещь.
    ItemDto addItem(Long ownerId, ItemDto itemDto);

    // Получаем вещь по ID.
    ItemDto getItemById(Long itemId);

    // Обновляем данные имеющейся вещи.
    ItemDto updateItem(Long id, Long ownerId, ItemDto updatedItemDto);

    // Удаляем имеющуюся вещь.
    ItemDto removeItem(Long ownerId, Long itemId);

    // Получаем список всех имеющихся вещей.
    List<ItemDto> getAllItems(Long ownerId);

    // Поиск вещей по содержанию определенного текста в названии или описании.
    List<ItemDto> searchItems(String text);

}