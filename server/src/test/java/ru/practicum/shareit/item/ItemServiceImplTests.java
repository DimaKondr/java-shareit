package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTests {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private UserDto ownerDto;
    private UserDto bookerDto;
    private UserDto anotherUserDto;
    private ItemDto itemDto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        ownerDto = new UserDto(null, "Владелец", "owner@email.com");
        bookerDto = new UserDto(null, "Бронирующий", "booker@yandex.ru");
        anotherUserDto = new UserDto(null, "Другой", "another@gmail.com");

        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);
        anotherUserDto = userService.addUser(anotherUserDto);

        itemDto = new ItemDto(null, "Тестовая вещь", "Тестовое описание", true,
                null, null, new ArrayList<>(), null);
        itemDto = itemService.addItem(ownerDto.getId(), itemDto);
    }

    @Test
    void testAddItem() {
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo("Тестовая вещь"));
        assertThat(itemDto.getDescription(), equalTo("Тестовое описание"));
        assertThat(itemDto.getAvailable(), equalTo(true));

        Item savedItem = itemRepository.findById(itemDto.getId()).orElseThrow();
        assertThat(savedItem.getOwnerId(), equalTo(ownerDto.getId()));
    }

    @Test
    void testAddItemWithNonExistentOwner() {
        ItemDto newItemDto = new ItemDto(null, "Новая вещь", "Описание", true,
                null, null, new ArrayList<>(), null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addItem(999L, newItemDto));

        assertThat(exception.getMessage(), containsString("Пользователь с ID: 999 не найден"));
    }

    @Test
    void testGetItemById() {
        ItemDto foundItem = itemService.getItemById(itemDto.getId());

        assertThat(foundItem, notNullValue());
        assertThat(foundItem.getId(), equalTo(itemDto.getId()));
        assertThat(foundItem.getName(), equalTo(itemDto.getName()));
        assertThat(foundItem.getComments(), empty());
    }

    @Test
    void testGetItemByIdWithComments() {
        User booker = userRepository.findById(bookerDto.getId()).orElseThrow();
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow();

        BookingCreateDto bookingDto = new BookingCreateDto(
                now.minusDays(10),
                now.minusDays(5),
                itemDto.getId(),
                booker.getId()
        );

        Booking booking = BookingMapper.dtoToBookingForCreate(bookingDto, item, booker);
        booking = bookingRepository.save(booking);
        bookingService.approveBooking(ownerDto.getId(), booking.getId(), "true");

        CommentCreateDto commentDto = new CommentCreateDto("Отличная вещь!");
        CommentResponseDto comment = itemService.addComment(bookerDto.getId(), commentDto, itemDto.getId());

        ItemDto itemWithComments = itemService.getItemById(itemDto.getId());

        assertThat(itemWithComments.getComments(), not(empty()));
        assertThat(itemWithComments.getComments().get(0).getText(), equalTo("Отличная вещь!"));
        assertThat(itemWithComments.getComments().get(0).getAuthor().getName(), equalTo(bookerDto.getName()));
    }

    @Test
    void testGetItemByIdNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999L));

        assertThat(exception.getMessage(), containsString("Вещь с ID: 999 не найдена"));
    }

    @Test
    void testUpdateItem() {
        ItemDto updateDto = new ItemDto(null, "Обновленное имя", "Обновленное описание", false,
                null, null, null, null);

        ItemDto updatedItem = itemService.updateItem(itemDto.getId(), ownerDto.getId(), updateDto);

        assertThat(updatedItem.getId(), equalTo(itemDto.getId()));
        assertThat(updatedItem.getName(), equalTo("Обновленное имя"));
        assertThat(updatedItem.getDescription(), equalTo("Обновленное описание"));
        assertThat(updatedItem.getAvailable(), equalTo(false));
    }

    @Test
    void testUpdateItemPartialUpdate() {
        ItemDto updateDto1 = new ItemDto(null, "Только имя", null, null,
                null, null, null, null);

        ItemDto updatedItem1 = itemService.updateItem(itemDto.getId(), ownerDto.getId(), updateDto1);

        assertThat(updatedItem1.getName(), equalTo("Только имя"));
        assertThat(updatedItem1.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(updatedItem1.getAvailable(), equalTo(itemDto.getAvailable()));

        ItemDto updateDto2 = new ItemDto(null, null, "Только описание", null,
                null, null, null, null);

        ItemDto updatedItem2 = itemService.updateItem(itemDto.getId(), ownerDto.getId(), updateDto2);

        assertThat(updatedItem2.getName(), equalTo("Только имя"));
        assertThat(updatedItem2.getDescription(), equalTo("Только описание"));
        assertThat(updatedItem2.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void testUpdateItemByNonOwner() {
        ItemDto updateDto = new ItemDto(null, "Обновление", "Описание", true,
                null, null, null, null);

        Long ownerId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(itemDto.getId(), ownerId, updateDto));

        assertThat(exception.getMessage(), containsString("Обновление вещи. Пользователь с ID: "
                + ownerId + " не найден"));
    }

    @Test
    void testUpdateNonExistentItem() {
        ItemDto updateDto = new ItemDto(null, "Обновление", "Описание", true,
                null, null, null, null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(999L, ownerDto.getId(), updateDto));

        assertThat(exception.getMessage(), containsString("Вещь с ID: 999не найдена"));
    }

    @Test
    void testRemoveItem() {
        ItemDto itemToRemove = new ItemDto(null, "На удаление", "Будет удалена", true,
                null, null, new ArrayList<>(), null);
        itemToRemove = itemService.addItem(ownerDto.getId(), itemToRemove);

        itemService.removeItem(ownerDto.getId(), itemToRemove.getId());

        Long itemToRemoveId = itemToRemove.getId();
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(itemToRemoveId));
    }

	@Test
	void testGetAllItems() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        UserDto userDto3 = new UserDto(null, "Ира", "ira@gmail.com");
	    userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        userDto3 = userService.addUser(userDto3);

        User testUser = userRepository.findById(userDto3.getId()).orElseThrow();

        assertThat(testUser.getId(), equalTo(userDto3.getId()));
        assertThat(testUser.getName(), equalTo(userDto3.getName()));
        assertThat(testUser.getEmail(), equalTo(userDto3.getEmail()));

        ItemDto itemDto1 = new ItemDto(null, "Перфоратор", "Супер инструмент", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Топор", "В лес по дрова", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto3 = new ItemDto(null, "Палатка", "Отдых на природе", true,
                null, null, new ArrayList<>(), null);

        ItemDto savedItemDto1 = itemService.addItem(userDto1.getId(), itemDto1);
        ItemDto savedItemDto2 = itemService.addItem(userDto1.getId(), itemDto2);
        ItemDto savedItemDto3 = itemService.addItem(userDto1.getId(), itemDto3);

        itemDto1.setId(savedItemDto1.getId());
        itemDto2.setId(savedItemDto2.getId());
        itemDto3.setId(savedItemDto3.getId());

        Item item1 = itemRepository.findById(savedItemDto2.getId()).orElseThrow();
        Item item2 = itemRepository.findById(savedItemDto3.getId()).orElseThrow();

        User booker1 = userRepository.findById(userDto2.getId()).orElseThrow();
        User booker2 = userRepository.findById(userDto3.getId()).orElseThrow();

        BookingCreateDto bookingDto1 = new BookingCreateDto(
                LocalDateTime.of(2024, 11, 20, 9, 15),
                LocalDateTime.of(2025, 3, 15, 15, 30),
                savedItemDto2.getId(),
                booker1.getId()
        );

        BookingCreateDto bookingDto2 = new BookingCreateDto(
                LocalDateTime.of(2027, 7, 20, 9, 15),
                LocalDateTime.of(2027, 7, 25, 15, 30),
                savedItemDto3.getId(),
                booker2.getId()
        );

        Booking bookingForSave1 = BookingMapper.dtoToBookingForCreate(bookingDto1, item1, booker1);
        Booking bookingForSave2 = BookingMapper.dtoToBookingForCreate(bookingDto2, item2, booker2);

        bookingForSave1 = bookingRepository.save(bookingForSave1);
        bookingForSave2 = bookingRepository.save(bookingForSave2);

        bookingService.approveBooking(userDto1.getId(), bookingForSave1.getId(), "true");
        bookingService.approveBooking(userDto1.getId(), bookingForSave2.getId(), "true");

        itemDto2.setLastBooking(bookingForSave1);
        itemDto3.setNextBooking(bookingForSave2);

        List<ItemDto> items = itemService.getAllItems(userDto1.getId());

        assertThat(items, allOf(notNullValue(), not(empty())));
        assertThat(items, allOf(hasItem(itemDto1), hasItem(itemDto2), hasItem(itemDto3)));
    }

    @Test
    void testGetAllItemsWithNonExistentOwner() {
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getAllItems(999L));

        assertThat(exception.getMessage(), containsString("Пользователь с ID: 999 не найден"));
    }

    @Test
    void testSearchItems() {
        ItemDto searchItem1 = new ItemDto(null, "Дрель ударная", "Профессиональный инструмент", true,
                null, null, new ArrayList<>(), null);
        ItemDto searchItem2 = new ItemDto(null, "Молоток", "Инструмент для забивания гвоздей", true,
                null, null, new ArrayList<>(), null);
        ItemDto searchItem3 = new ItemDto(null, "Отвертка", "Крестовая отвертка", true,
                null, null, new ArrayList<>(), null);

        itemService.addItem(ownerDto.getId(), searchItem1);
        itemService.addItem(ownerDto.getId(), searchItem2);
        itemService.addItem(ownerDto.getId(), searchItem3);

        List<ItemDto> searchByName = itemService.searchItems("Дрель");
        assertThat(searchByName, hasSize(1));
        assertThat(searchByName.get(0).getName(), containsString("Дрель"));

        List<ItemDto> searchByDescription = itemService.searchItems("гвоздей");
        assertThat(searchByDescription, hasSize(1));
        assertThat(searchByDescription.get(0).getName(), equalTo("Молоток"));

        List<ItemDto> emptySearch = itemService.searchItems("");
        assertThat(emptySearch, empty());

        List<ItemDto> nullSearch = itemService.searchItems(null);
        assertThat(nullSearch, empty());

        List<ItemDto> noResults = itemService.searchItems("Несуществующий текст");
        assertThat(noResults, empty());
    }

    @Test
    void testAddComment() {
        User booker = userRepository.findById(bookerDto.getId()).orElseThrow();
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow();

        BookingCreateDto bookingDto = new BookingCreateDto(
                now.minusDays(10),
                now.minusDays(5),
                itemDto.getId(),
                booker.getId()
        );

        Booking booking = BookingMapper.dtoToBookingForCreate(bookingDto, item, booker);
        booking = bookingRepository.save(booking);
        bookingService.approveBooking(ownerDto.getId(), booking.getId(), "true");

        CommentCreateDto commentDto = new CommentCreateDto("Отличная вещь, все работает!");
        CommentResponseDto comment = itemService.addComment(bookerDto.getId(), commentDto, itemDto.getId());

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getText(), equalTo("Отличная вещь, все работает!"));
        assertThat(comment.getAuthorName(), equalTo(bookerDto.getName()));
        assertThat(comment.getCreated(), notNullValue());
    }

    @Test
    void testAddCommentWithoutCompletedBooking() {
        CommentCreateDto commentDto = new CommentCreateDto("Попытка комментария");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(bookerDto.getId(), commentDto, itemDto.getId()));

        assertThat(exception.getMessage(),
                containsString("Оставить комментарий к вещи может только бронировавший ее пользователь"));
    }

    @Test
    void testAddCommentByNonBooker() {
        User booker = userRepository.findById(bookerDto.getId()).orElseThrow();
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow();

        BookingCreateDto bookingDto = new BookingCreateDto(
                now.minusDays(10),
                now.minusDays(5),
                itemDto.getId(),
                booker.getId()
        );

        Booking booking = BookingMapper.dtoToBookingForCreate(bookingDto, item, booker);
        booking = bookingRepository.save(booking);
        bookingService.approveBooking(ownerDto.getId(), booking.getId(), "true");

        CommentCreateDto commentDto = new CommentCreateDto("Комментарий от чужого");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(anotherUserDto.getId(), commentDto, itemDto.getId()));

        assertThat(exception.getMessage(),
                containsString("Оставить комментарий к вещи может только бронировавший ее пользователь"));
    }

    @Test
    void testAddCommentToNonExistentItem() {
        CommentCreateDto commentDto = new CommentCreateDto("Комментарий");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(bookerDto.getId(), commentDto, 999L));

        assertThat(exception.getMessage(), containsString("Оставить комментарий к вещи может " +
                "только бронировавший ее пользователь, и только для завершенного бронирования."));
    }

    @Test
    void testAddCommentByNonExistentUser() {
        CommentCreateDto commentDto = new CommentCreateDto("Комментарий");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.addComment(999L, commentDto, itemDto.getId()));

        assertThat(exception.getMessage(), containsString("Оставить комментарий к вещи может " +
                "только бронировавший ее пользователь, и только для завершенного бронирования."));
    }

}