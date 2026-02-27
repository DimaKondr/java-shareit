package ru.practicum.shareit.item.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Добавление вещи. " +
                "Пользователь с ID: " + ownerId + " не найден"));
        Item addedItem = ItemMapper.dtoToItem(itemDto.getId(), ownerId, itemDto);
        addedItem.setOwnerId(ownerId);
        itemRepository.save(addedItem);
        return ItemMapper.itemToDto(addedItem);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            log.error("Ошибка. Вещь с ID: {} не найдена.", itemId);
            return new NotFoundException("Вещь с ID: " + itemId + " не найдена.");
        });
        List<Comment> comments = commentRepository.findCommentsOfItem(itemId);
        return ItemMapper.itemToDtoWithCommentsOrBookings(item, null, null, comments);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long ownerId, ItemDto updatedItemDto) {
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь с ID: "
                + itemId + "не найдена."));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Обновление вещи. " +
                "Пользователь с ID: " + ownerId + " не найден"));

        Item updatedItem = ItemMapper.dtoToItem(itemId, ownerId, updatedItemDto);

        if (updatedItem.getName() == null || updatedItem.getName().isBlank()) {
            log.debug("Обновление вещи. Имя не указано. Оставляем имя без изменений");
            updatedItem.setName(oldItem.getName());
        }

        if (updatedItem.getDescription() == null || updatedItem.getDescription().isBlank()) {
            log.debug("Обновление вещи. Описание не указано. Оставляем описание без изменений");
            updatedItem.setDescription(oldItem.getDescription());
        }

        if (updatedItem.getAvailable() == null || updatedItem.getDescription().isBlank()) {
            log.debug("Обновление вещи. Доступность не указана. Оставляем доступность без изменений");
            updatedItem.setAvailable(oldItem.getAvailable());
        }

        itemRepository.save(updatedItem);
        return ItemMapper.itemToDto(updatedItem);
    }

    @Override
    public void removeItem(Long ownerId, Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException(
                "Получение всех вещей пользователя. Пользователь с ID: " + ownerId + " не найден"));

        List<Item> itemsOfUser = itemRepository.findByOwnerId(ownerId);
        List<ItemDto> result = new ArrayList<>();

        for (Item item : itemsOfUser) {
            Booking lastBooking = getLastBooking(item.getId());
            Booking nextBooking = getNextBooking(item.getId());
            List<Comment> comments = commentRepository.findCommentsOfItem(item.getId());
            ItemDto resultDto = ItemMapper.itemToDtoWithCommentsOrBookings(item, lastBooking,
                    nextBooking, comments);
            result.add(resultDto);
        }
        return result;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.searchItems(text);
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long authorId, CommentCreateDto commentDto, Long itemId) {
        Pageable pageable = PageRequest.of(0, 1);
        List<Booking> booking = bookingRepository.findBookingForComment(itemId, authorId, pageable);

        if (booking.isEmpty()) {
            log.error("Попытка создания комментария к вещи для незавершенного бронирования " +
                    "или пользователем, не бронировавшим вещь.");
            throw new ValidationException("Оставить комментарий к вещи может только бронировавший ее пользователь, " +
                    "и только для завершенного бронирования.");
        } else {
            User author = userRepository.findById(authorId).orElseThrow();
            Item item = itemRepository.findById(itemId).orElseThrow();
            Comment addedComment = CommentMapper.dtoToCommentForCreate(commentDto, item, author);
            addedComment = commentRepository.save(addedComment);
            return CommentMapper.commentToDtoForResponse(addedComment);
        }
    }

    private Booking getLastBooking(Long itemId) {
            Pageable pageable = PageRequest.of(0, 1);
            List<Booking> results = bookingRepository.findByItemIdLastBooking(itemId, LocalDateTime.now(), pageable);
            return results.isEmpty() ? null : results.get(0);
    }

    private Booking getNextBooking(Long itemId) {
            Pageable pageable = PageRequest.of(0, 1);
            List<Booking> results = bookingRepository.findByItemIdNextBooking(itemId, LocalDateTime.now(), pageable);
            return results.isEmpty() ? null : results.get(0);
    }

}