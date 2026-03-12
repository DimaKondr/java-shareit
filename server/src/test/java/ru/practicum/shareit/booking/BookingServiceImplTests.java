package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
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
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTests {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void testAddBooking_Success() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto result = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), notNullValue());
        assertThat(result.getStart(), equalTo(start));
        assertThat(result.getEnd(), equalTo(end));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(result.getBooker().getId(), equalTo(bookerDto.getId()));
        assertThat(result.getItem().getId(), equalTo(savedItemDto.getId()));

        Item item = itemRepository.findById(savedItemDto.getId()).orElseThrow();
        assertFalse(item.getAvailable());
    }

    @Test
    void testAddBooking_StartInPast_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        Long bookerId = bookerDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Начало бронирования не может быть в прошлом"));
    }

    @Test
    void testAddBooking_EndInPast_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1); // окончание в прошлом

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        Long bookerId = bookerDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Окончание бронирования не может быть в прошлом"));
    }

    @Test
    void testAddBooking_EndBeforeStart_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        Long bookerId = bookerDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Окончание бронирования не может быть раньше его начала"));
    }

    @Test
    void testAddBooking_StartEqualsEnd_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        LocalDateTime start = sameTime;
        LocalDateTime end = sameTime;

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        Long bookerId = bookerDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Начало и окончание бронирования не могут совпадать по времени"));
    }

    @Test
    void testAddBooking_ItemNotAvailable_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", false, // недоступна
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        Long bookerId = bookerDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookerId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Данная вещь недоступна для бронирования"));
    }

    @Test
    void testAddBooking_UserNotFound_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        ownerDto = userService.addUser(ownerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long nonExistentUserId = 999L;

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), nonExistentUserId
        );

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(nonExistentUserId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Пользователь с ID: " + nonExistentUserId + " не найден"));
    }

    @Test
    void testAddBooking_ItemNotFound_ThrowsException() {
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        bookerDto = userService.addUser(bookerDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Long nonExistentItemId = 999L;

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, nonExistentItemId, bookerDto.getId()
        );

        Long bookerId = bookerDto.getId();
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(bookerId, bookingCreateDto));

        assertThat(exception.getMessage(),
                containsString("Вещь с ID: " + nonExistentItemId + " не найдена"));
    }

    @Test
    void testApproveBooking_ApproveSuccess() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        BookingResponseDto result = bookingService.approveBooking(
                ownerDto.getId(), createdBooking.getId(), "true");

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));

        Booking updatedBooking = bookingRepository.findById(createdBooking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testApproveBooking_RejectSuccess() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        BookingResponseDto result = bookingService.approveBooking(
                ownerDto.getId(), createdBooking.getId(), "false");

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.REJECTED));

        Booking updatedBooking = bookingRepository.findById(createdBooking.getId()).orElseThrow();
        assertThat(updatedBooking.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void testApproveBooking_NotOwner_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        UserDto otherUserDto = new UserDto(null, "Петр", "petr@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);
        otherUserDto = userService.addUser(otherUserDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        Long otherUserId = otherUserDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(
                        otherUserId, createdBooking.getId(), "true"));

        assertThat(exception.getMessage(),
                containsString("Подтвердить бронирование может только владелец вещи"));
    }

    @Test
    void testApproveBooking_BookingNotFound_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        ownerDto = userService.addUser(ownerDto);

        Long nonExistentBookingId = 999L;

        Long ownerId = ownerDto.getId();
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(
                        ownerId, nonExistentBookingId, "true"));

        assertThat(exception.getMessage(),
                containsString("Бронирование с ID: " + nonExistentBookingId + " не найдено"));
    }

    @Test
    void testApproveBooking_UserNotFound_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        Long nonExistentUserId = 999L;

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(
                        nonExistentUserId, createdBooking.getId(), "true"));

        assertThat(exception.getMessage(),
                containsString("Пользователь с ID: " + nonExistentUserId + " не найден"));
    }

    @Test
    void testGetBookingById_SuccessAsBooker() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        BookingResponseDto result = bookingService.getBookingById(bookerDto.getId(), createdBooking.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(createdBooking.getId()));
        assertThat(result.getBooker().getId(), equalTo(bookerDto.getId()));
        assertThat(result.getItem().getId(), equalTo(savedItemDto.getId()));
    }

    @Test
    void testGetBookingById_SuccessAsOwner() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        BookingResponseDto result = bookingService.getBookingById(ownerDto.getId(), createdBooking.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(createdBooking.getId()));
    }

    @Test
    void testGetBookingById_NotAuthorized_ThrowsException() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto = new UserDto(null, "Анна", "anna@email.com");
        UserDto otherUserDto = new UserDto(null, "Петр", "petr@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto = userService.addUser(bookerDto);
        otherUserDto = userService.addUser(otherUserDto);

        ItemDto itemDto = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto savedItemDto = itemService.addItem(ownerDto.getId(), itemDto);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(
                start, end, savedItemDto.getId(), bookerDto.getId()
        );

        BookingResponseDto createdBooking = bookingService.addBooking(bookerDto.getId(), bookingCreateDto);

        Long otherUserId = otherUserDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getBookingById(otherUserId, createdBooking.getId()));

        assertThat(exception.getMessage(),
                containsString("Посмотреть данные бронирования может только владелец вещи " +
                        "или пользователь, совершивший бронирование"));
    }

    @Test
    void testGetBookingById_BookingNotFound_ThrowsException() {
        UserDto userDto = new UserDto(null, "Анна", "anna@email.com");
        userDto = userService.addUser(userDto);

        Long nonExistentBookingId = 999L;

        Long userId = userDto.getId();
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(userId, nonExistentBookingId));

        assertThat(exception.getMessage(),
                containsString("Бронирование с ID: " + nonExistentBookingId + " не найдено"));
    }

    @Test
    void testGetBookingsMadeByUser() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        UserDto userDto3 = new UserDto(null, "Ира", "ira@gmail.com");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        userDto3 = userService.addUser(userDto3);

        ItemDto itemDto1 = new ItemDto(null, "Перфоратор", "Супер инструмент", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Топор", "В лес по дрова", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto3 = new ItemDto(null, "Палатка", "Отдых на природе", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto4 = new ItemDto(null, "Самовар", "Варит вкусный чай", true,
                null, null, new ArrayList<>(), null);

        ItemDto savedItemDto1 = itemService.addItem(userDto2.getId(), itemDto1);
        ItemDto savedItemDto2 = itemService.addItem(userDto2.getId(), itemDto2);
        ItemDto savedItemDto3 = itemService.addItem(userDto3.getId(), itemDto3);
        ItemDto savedItemDto4 = itemService.addItem(userDto3.getId(), itemDto4);

        Item item1 = itemRepository.findById(savedItemDto1.getId()).orElseThrow();
        Item item2 = itemRepository.findById(savedItemDto2.getId()).orElseThrow();
        Item item3 = itemRepository.findById(savedItemDto3.getId()).orElseThrow();
        Item item4 = itemRepository.findById(savedItemDto4.getId()).orElseThrow();

        User booker = userRepository.findById(userDto1.getId()).orElseThrow();

        BookingCreateDto bookingCreateDto1 = new BookingCreateDto(
                LocalDateTime.of(2025, 2, 20, 0, 0),
                LocalDateTime.of(2025, 3, 21, 23, 59),
                savedItemDto1.getId(),
                userDto1.getId()
        );

        BookingCreateDto bookingCreateDto2 = new BookingCreateDto(
                LocalDateTime.of(2027, 7, 1, 9, 15),
                LocalDateTime.of(2027, 8, 1, 15, 30),
                savedItemDto2.getId(),
                userDto1.getId()
        );

        BookingCreateDto bookingCreateDto3 = new BookingCreateDto(
                LocalDateTime.of(2025, 2, 19, 23, 59),
                LocalDateTime.of(2025, 3, 20, 0, 0),
                savedItemDto3.getId(),
                userDto1.getId()
        );

        BookingCreateDto bookingCreateDto4 = new BookingCreateDto(
                LocalDateTime.of(2025, 2, 15, 9, 15),
                LocalDateTime.of(2025, 3, 16, 15, 30),
                savedItemDto4.getId(),
                userDto1.getId()
        );

        Booking bookingForSave1 = BookingMapper.dtoToBookingForCreate(bookingCreateDto1, item1, booker);
        Booking bookingForSave2 = BookingMapper.dtoToBookingForCreate(bookingCreateDto2, item2, booker);
        Booking bookingForSave3 = BookingMapper.dtoToBookingForCreate(bookingCreateDto3, item3, booker);
        Booking bookingForSave4 = BookingMapper.dtoToBookingForCreate(bookingCreateDto4, item4, booker);

        bookingForSave1 = bookingRepository.save(bookingForSave1);
        bookingForSave2 = bookingRepository.save(bookingForSave2);
        bookingForSave3 = bookingRepository.save(bookingForSave3);
        bookingForSave4 = bookingRepository.save(bookingForSave4);

        bookingService.approveBooking(userDto2.getId(), bookingForSave1.getId(), "true");
        bookingService.approveBooking(userDto3.getId(), bookingForSave3.getId(), "false");
        bookingService.approveBooking(userDto3.getId(), bookingForSave4.getId(), "true");

        BookingResponseDto testBooking1 = bookingService.getBookingById(userDto1.getId(), bookingForSave1.getId());
        BookingResponseDto testBooking2 = bookingService.getBookingById(userDto1.getId(), bookingForSave2.getId());
        BookingResponseDto testBooking3 = bookingService.getBookingById(userDto1.getId(), bookingForSave3.getId());
        BookingResponseDto testBooking4 = bookingService.getBookingById(userDto1.getId(), bookingForSave4.getId());

        Long bookerId = userDto1.getId();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getBookingsMadeByUser(bookerId, ""));

        assertThat(exception.getMessage(),
                containsString("Неверно задано значение статуса."));

        List<BookingResponseDto> allBookings = bookingService.getBookingsMadeByUser(bookerId, "all");

        assertThat(allBookings, allOf(notNullValue(), not(empty())));
        assertThat(allBookings, contains(testBooking2, testBooking1, testBooking3, testBooking4));

        List<BookingResponseDto> waitingBookings = bookingService.getBookingsMadeByUser(bookerId, "waiting");

        assertThat(waitingBookings, allOf(notNullValue(), not(empty())));
        assertThat(waitingBookings, contains(testBooking2));

        List<BookingResponseDto> pastBookings = bookingService.getBookingsMadeByUser(bookerId, "past");
        assertThat(pastBookings, allOf(notNullValue(), not(empty())));
        assertThat(pastBookings, contains(testBooking1, testBooking3, testBooking4));

        List<BookingResponseDto> futureBookings = bookingService.getBookingsMadeByUser(bookerId, "future");
        assertThat(futureBookings, allOf(notNullValue(), not(empty())));
        assertThat(futureBookings, contains(testBooking2));

        List<BookingResponseDto> rejectedBookings = bookingService.getBookingsMadeByUser(bookerId, "rejected");
        assertThat(rejectedBookings, allOf(notNullValue(), not(empty())));
        assertThat(rejectedBookings, contains(testBooking3));

        List<BookingResponseDto> emptyBookings = bookingService.getBookingsMadeByUser(userDto2.getId(), "all");
        assertThat(emptyBookings, empty());
    }

    @Test
    void testGetBookingsOfUserItems_AllStates() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        UserDto bookerDto1 = new UserDto(null, "Анна", "anna@email.com");
        UserDto bookerDto2 = new UserDto(null, "Петр", "petr@email.com");
        ownerDto = userService.addUser(ownerDto);
        bookerDto1 = userService.addUser(bookerDto1);
        bookerDto2 = userService.addUser(bookerDto2);

        ItemDto itemDto1 = new ItemDto(null, "Дрель", "Электрическая дрель", true,
                null, null, new ArrayList<>(), null);
        ItemDto itemDto2 = new ItemDto(null, "Перфоратор", "Мощный инструмент", true,
                null, null, new ArrayList<>(), null);

        ItemDto savedItemDto1 = itemService.addItem(ownerDto.getId(), itemDto1);
        ItemDto savedItemDto2 = itemService.addItem(ownerDto.getId(), itemDto2);

        Item item1 = itemRepository.findById(savedItemDto1.getId()).orElseThrow();
        Item item2 = itemRepository.findById(savedItemDto2.getId()).orElseThrow();

        User booker1 = userRepository.findById(bookerDto1.getId()).orElseThrow();
        User booker2 = userRepository.findById(bookerDto2.getId()).orElseThrow();

        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = new Booking(null, now.minusDays(10), now.minusDays(5),
                item1, booker1, BookingStatus.APPROVED);

        Booking currentBooking = new Booking(null, now.minusDays(1), now.plusDays(5),
                item1, booker2, BookingStatus.APPROVED);

        Booking futureBooking = new Booking(null, now.plusDays(10), now.plusDays(15),
                item2, booker1, BookingStatus.WAITING);

        Booking rejectedBooking = new Booking(null, now.plusDays(20), now.plusDays(25),
                item2, booker2, BookingStatus.REJECTED);

        pastBooking = bookingRepository.save(pastBooking);
        currentBooking = bookingRepository.save(currentBooking);
        futureBooking = bookingRepository.save(futureBooking);
        rejectedBooking = bookingRepository.save(rejectedBooking);

        List<BookingResponseDto> allBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "ALL");
        assertThat(allBookings, notNullValue());
        assertThat(allBookings, hasSize(4));

        List<BookingResponseDto> currentBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "CURRENT");
        assertThat(currentBookings, notNullValue());
        assertThat(currentBookings, hasSize(1));
        assertThat(currentBookings.get(0).getId(), equalTo(currentBooking.getId()));

        List<BookingResponseDto> pastBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "PAST");
        assertThat(pastBookings, notNullValue());
        assertThat(pastBookings, hasSize(1));
        assertThat(pastBookings.get(0).getId(), equalTo(pastBooking.getId()));

        List<BookingResponseDto> futureBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "FUTURE");
        assertThat(futureBookings, notNullValue());
        assertThat(futureBookings, hasSize(2));

        List<BookingResponseDto> waitingBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "WAITING");
        assertThat(waitingBookings, notNullValue());
        assertThat(waitingBookings, hasSize(1));
        assertThat(waitingBookings.get(0).getId(), equalTo(futureBooking.getId()));

        List<BookingResponseDto> rejectedBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "REJECTED");
        assertThat(rejectedBookings, notNullValue());
        assertThat(rejectedBookings, hasSize(1));
        assertThat(rejectedBookings.get(0).getId(), equalTo(rejectedBooking.getId()));

        Long ownerId = ownerDto.getId();
        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.getBookingsOfUserItems(ownerId, "INVALID"));

        assertThat(exception.getMessage(),
                containsString("Неверно задано значение статуса"));
    }

    @Test
    void testGetBookingsOfUserItems_UserNotFound_ThrowsException() {
        Long nonExistentUserId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsOfUserItems(nonExistentUserId, "ALL"));

        assertThat(exception.getMessage(),
                containsString("Пользователь с ID: " + nonExistentUserId + " не найден"));
    }

    @Test
    void testGetBookingsOfUserItems_EmptyResult() {
        UserDto ownerDto = new UserDto(null, "Влад", "vlad@email.com");
        ownerDto = userService.addUser(ownerDto);

        List<BookingResponseDto> allBookings = bookingService.getBookingsOfUserItems(ownerDto.getId(), "ALL");

        assertThat(allBookings, empty());
    }

    @Test
    void testGetBookingsMadeByUser_UserNotFound_ThrowsException() {
        Long nonExistentUserId = 999L;

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsMadeByUser(nonExistentUserId, "ALL"));

        assertThat(exception.getMessage(),
                containsString("Пользователь с ID: " + nonExistentUserId + " не найден"));
    }

}