package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("inMemoryItemDao")
@Slf4j
public class InMemoryItemDao implements ItemDao {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        if (item == null) {
            log.error("Запрос на добавление новой вещи поступил с пустым телом");
            throw new ValidationException("Запрос на добавление вещи поступил с пустым телом");
        }
        item.setId(getNextId());
        log.debug("Новой вещи назначен ID: {}", item.getId());
        items.put(item.getId(), item);
        log.info("Успешно добавлена новая вещь с ID: {}", item.getId());
        return item;
    }

    @Override
    public Item getItemById(Long itemId) {
        log.info("Начата проверка наличия вещи с ID: {} для ее предоставления по запросу", itemId);
        if (itemId != null && items.containsKey(itemId)) {
            log.info("Вещь с ID: {} найдена и успешно предоставлена в ответ на запрос.", itemId);
            return items.get(itemId);
        }
        log.error("Попытка получения вещи по ID. Вещь с ID: {} не найдена.", itemId);
        throw new NotFoundException("Попытка получения вещи. Вещь с ID: " + itemId + " не найдена.");
    }

    @Override
    public Item updateItem(Long itemId, Item updatedItem) {
        if (updatedItem == null) {
            log.error("Запрос на обновление данных вещи поступил с пустым телом");
            throw new ValidationException("Запрос на обновление данных вещи поступил с пустым телом");
        }
        log.info("Начат процесс обновления данных вещи.");
        log.info("Начата проверка наличия вещи с ID: {}", itemId);
        if (items.containsKey(itemId)) {
            Item oldItem = items.get(itemId);

            if (!oldItem.getOwnerId().equals(updatedItem.getOwnerId())) {
                log.error("Предоставленный при обновлении ID владельца вещи отличается от фактического. " +
                        "Предоставленный ID владельца: {} >>> Фактический ID владельца: {}.",
                        updatedItem.getOwnerId(), oldItem.getOwnerId());
                throw new NotFoundException("Пользователь с ID: " + updatedItem.getOwnerId() +
                        " не является владельцем вещи.");
            }
            if (!oldItem.getName().equals(updatedItem.getName()) && updatedItem.getName() != null
                    && !updatedItem.getName().isBlank()) {
                log.debug("Устанавливаем обновленное название вещи с ID: {}", itemId);
                oldItem.setName(updatedItem.getName());
            }
            if (!oldItem.getDescription().equals(updatedItem.getDescription()) && updatedItem.getDescription() != null
                    && !updatedItem.getDescription().isBlank()) {
                log.debug("Устанавливаем обновленное описание вещи с ID: {}", itemId);
                oldItem.setDescription(updatedItem.getDescription());
            }
            if (!oldItem.getAvailable().equals(updatedItem.getAvailable())
                    && updatedItem.getAvailable() != null) {
                log.debug("Устанавливаем обновленный статус доступности вещи с ID: {}", itemId);
                oldItem.setAvailable(updatedItem.getAvailable());
            }
            log.info("Данные вещи с ID: {} успешно обновлены", itemId);
            return oldItem;
        }
        log.error("Попытка обновления данных вещи. Вещь с ID: {} не найдена", itemId);
        throw new NotFoundException("Попытка обновления данных вещи. Вещь с ID: " + itemId + " не найдена.");
    }

    @Override
    public Item removeItem(Long ownerId, Long itemId) {
        log.info("Начата проверка наличия вещи с ID: {} для ее последующего удаления", itemId);
        if (items.containsKey(itemId)) {
            Item removedItem = items.get(itemId);

            if (!removedItem.getOwnerId().equals(ownerId)) {
                log.error("Предоставленный при удалении вещи ID владельца вещи отличается от фактического. " +
                                "Предоставленный ID владельца: {} >>> Фактический ID владельца: {}.",
                        ownerId, removedItem.getOwnerId());
                throw new ValidationException("Пользователь с ID: " + ownerId +
                        " не является владельцем вещи.");
            }
            log.info("Вещь с ID: {} успешно удалена.", itemId);
            return items.remove(itemId);
        }
        log.error("Попытка удаления пользователя. Пользователь с ID: {} не найден", itemId);
        throw new NotFoundException("Попытка удаления пользователя. Пользователь с ID: " + itemId + " не найден");
    }

    @Override
    public List<Item> getAllItems(Long ownerId) {
        log.info("Начат процесс предоставления списка всех вещей пользователя с ID: {}", ownerId);
        return items.values().stream()
                .filter(item -> ownerId.equals(item.getOwnerId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Начат процесс поиска вещей по включению необходимого текста.");
        if (text == null || text.isBlank()) {
            log.info("Переданный текст null или пуст. Возвращаем пустой список.");
            return Collections.emptyList();
        }
        String lowerText = text.toLowerCase();
        log.info("Возвращаем список искомых вещей.");
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(lowerText))
                        || (item.getDescription() != null
                                && item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }

    // Генерируем ID нового пользователя.
    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}