package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {

    public static BookingResponseDto bookingToDtoForResponse(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static Booking dtoToBookingForCreate(BookingCreateDto bookingCreateDto, Item item, User booker) {
        return new Booking(
                bookingCreateDto.getStart(),
                bookingCreateDto.getEnd(),
                item,
                booker
        );
    }

}