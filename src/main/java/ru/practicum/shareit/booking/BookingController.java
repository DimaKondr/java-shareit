package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.HttpHeaderNames;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto addBooking(@RequestHeader(HttpHeaderNames.USER_ID) Long bookerId,
                                         @RequestBody
                                             @NotNull(message = "booking не может быть null")
                                             @Valid BookingCreateDto bookingCreateDto) {
        return bookingService.addBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(HttpHeaderNames.USER_ID) Long approverId,
                                             @PathVariable("bookingId")
                                                 @NotNull(message = "bookingId не может быть null")
                                                 @Valid Long bookingId,
                                             @RequestParam(name = "approved")
                                                 @Pattern(regexp = "^(true|false)$",
                                                         message = "Параметр должен быть 'true' или 'false'")
                                                 @Valid String approved) {
        return bookingService.approveBooking(approverId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader(HttpHeaderNames.USER_ID) Long userId,
                                             @PathVariable("bookingId")
                                                 @NotNull(message = "bookingId не может быть null")
                                                 @Valid Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsMadeByUser(@RequestHeader(HttpHeaderNames.USER_ID) Long userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                       @Pattern(regexp = "^(ALL|CURRENT|PAST|FUTURE|WAITING|REJECTED)$",
                                                                      message = "Недопустимое значение параметра.")
                                                       @Valid String state) {
        return bookingService.getBookingsMadeByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOfUserItems(@RequestHeader(HttpHeaderNames.USER_ID) Long userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                       @Pattern(regexp = "^(ALL|CURRENT|PAST|FUTURE|WAITING|REJECTED)$",
                                                               message = "Недопустимое значение параметра.")
                                                       @Valid String state) {
        return bookingService.getBookingsOfUserItems(userId, state);
    }

}