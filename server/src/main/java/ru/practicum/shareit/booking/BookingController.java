package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.HttpHeaderNames;

import java.util.List;

@RestController
@RequestMapping("/bookings")
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
                                         @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingService.addBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader(HttpHeaderNames.USER_ID) Long approverId,
                                             @PathVariable("bookingId") Long bookingId,
                                             @RequestParam(name = "approved") String approved) {
        return bookingService.approveBooking(approverId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader(HttpHeaderNames.USER_ID) Long userId,
                                             @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsMadeByUser(@RequestHeader(HttpHeaderNames.USER_ID) Long userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "all")
                                                       String state) {
        return bookingService.getBookingsMadeByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsOfUserItems(@RequestHeader(HttpHeaderNames.USER_ID) Long userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                       String state) {
        return bookingService.getBookingsOfUserItems(userId, state);
    }

}