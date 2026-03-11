package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
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

}