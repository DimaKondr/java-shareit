package ru.practicum.shareit.item.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userDao.getUserById(ownerId);
        Item addedItem = ItemMapper.dtoToItem(itemDto.getId(), ownerId, itemDto);
        itemDao.addItem(addedItem);
        return ItemMapper.itemToDto(addedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemDao.getItemById(itemId);
        return ItemMapper.itemToDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long ownerId, ItemDto updatedItemDto) {
        Item updatedItem = ItemMapper.dtoToItem(itemId, ownerId, updatedItemDto);
        itemDao.updateItem(itemId, updatedItem);
        return ItemMapper.itemToDto(updatedItem);
    }

    @Override
    public ItemDto removeItem(Long ownerId, Long itemId) {
        Item removedItem = itemDao.removeItem(ownerId, itemId);
        return ItemMapper.itemToDto(removedItem);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        List<Item> items = itemDao.getAllItems(ownerId);
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = itemDao.searchItems(text);
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

}