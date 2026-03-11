package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemDtoForCreating() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Самовар", "Самовар электрический", true, 15L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(15);
    }

    @Test
    void testItemDtoWithInfo() throws Exception {
        Item item = new Item(12L, "Самовар", "Самовар электрический", true, 4L, 2L);
        User booker = new User(11L, "Коля", "nik@email.com");

        Booking lastBooking = new Booking(7L, LocalDateTime.of(2026, 1, 10, 12, 37,52),
                LocalDateTime.of(2026, 2, 10, 12, 37,52), item, booker, BookingStatus.APPROVED);
        Booking nextBooking = new Booking(8L, LocalDateTime.of(2026, 8, 10, 12, 37,52),
                LocalDateTime.of(2026, 9, 10, 12, 37,52), item, booker, BookingStatus.APPROVED);

        Comment comment = new Comment("Хороший самовар", item, booker);

        ItemDto itemDto = new ItemDto(1L, "Самовар", "Самовар электрический", true, lastBooking,
                nextBooking, List.of(comment), 15L);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(7);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(LocalDateTime.of(2026, 1, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(LocalDateTime.of(2026, 2, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.id").isEqualTo(12);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.item.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.item.description")
                .isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.lastBooking.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.ownerId").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.booker.id").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.name").isEqualTo("Коля");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.email").isEqualTo("nik@email.com");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo(BookingStatus.APPROVED.toString());

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(8);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start")
                .isEqualTo(LocalDateTime.of(2026, 8, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end")
                .isEqualTo(LocalDateTime.of(2026, 9, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.id").isEqualTo(12);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.item.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.item.description")
                .isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.nextBooking.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.ownerId").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.booker.id").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.booker.name").isEqualTo("Коля");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.booker.email").isEqualTo("nik@email.com");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status")
                .isEqualTo(BookingStatus.APPROVED.toString());

        assertThat(result).extractingJsonPathStringValue("$.comments.[0].text").isEqualTo("Хороший самовар");
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].item.id").isEqualTo(12);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].item.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].item.description")
                .isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.comments.[0].item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].item.ownerId").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].item.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.comments.[0].author.id").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].author.name").isEqualTo("Коля");
        assertThat(result).extractingJsonPathStringValue("$.comments.[0].author.email").isEqualTo("nik@email.com");

        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(15);
    }

}