package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    // Добавляем новое бронирование.
    BookingResponseDto addBooking(Long bookerId, BookingCreateDto bookingCreateDto);

    // Подтверждаем или отклоняем запрос на бронирование.
    BookingResponseDto approveBooking(Long approverId, Long bookingId, String approved);

    // Получаем бронирование по ID.
    BookingResponseDto getBookingById(Long userId, Long bookingId);

    // Получаем список всех бронирований пользователя.
    List<BookingResponseDto> getBookingsMadeByUser(Long userId, String state);

    // Получаем список бронирований для всех вещей пользователя.
    List<BookingResponseDto> getBookingsOfUserItems(Long userId, String state);

}