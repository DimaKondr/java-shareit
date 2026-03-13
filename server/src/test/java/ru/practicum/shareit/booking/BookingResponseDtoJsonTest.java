package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingResponseDtoJsonTest {
    private final JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingResponseDto() throws Exception {
        Item item = new Item(12L, "Самовар", "Самовар электрический", true, 4L, 2L);
        User booker = new User(11L, "Коля", "nik@email.com");

        BookingResponseDto bookingResponseDto = new BookingResponseDto(23L, LocalDateTime.of(2026, 8, 10, 12, 37,52),
                LocalDateTime.of(2026, 9, 10, 12, 37,52), item, booker, BookingStatus.APPROVED);

        JsonContent<BookingResponseDto> result = json.write(bookingResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(23);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(LocalDateTime.of(2026, 8, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(LocalDateTime.of(2026, 9, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(12);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Коля");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("nik@email.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(BookingStatus.APPROVED.toString());
    }

}