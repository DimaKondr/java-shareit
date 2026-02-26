package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Long id;

    @NotNull(message = "Дата начала бронирования не может быть null")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    private LocalDateTime end;

    @NotNull(message = "ID бронируемой вещи не может быть null")
    private Item item;

    @NotNull(message = "ID бронирующего пользователя не может быть null")
    private User booker;

    @NotNull(message = "Статус бронирования не может быть null")
    private BookingStatus status;
}