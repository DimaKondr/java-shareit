package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {

    @NotNull(message = "Дата начала бронирования не может быть null")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    private LocalDateTime end;

    @NotNull(message = "ID бронируемой вещи не может быть null")
    private Long itemId;
    private User booker;
}