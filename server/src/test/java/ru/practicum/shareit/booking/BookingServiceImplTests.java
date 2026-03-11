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
import ru.practicum.shareit.booking.service.BookingService;
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
    }

}