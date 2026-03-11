package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @NotNull(message = "booking can not be null")
                @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.addBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long approverId,
            @PathVariable("bookingId")
                @NotNull(message = "bookingId can not be null") Long bookingId,
            @RequestParam(name = "approved")
                @Pattern(regexp = "^(true|false)$", message = "requestParameter should be 'true' or 'false'")
                String approved) {
        log.info("Approving booking {}, userId={}, approveStatus={}", bookingId, approverId, approved);
        return bookingClient.approveBooking(approverId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable @NotNull(message = "bookingId can not be null")
                                                     Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
	public ResponseEntity<Object> getBookingsMadeByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
			@RequestParam(name = "state", required = false, defaultValue = "all")
            @Pattern(regexp = "^(all|current|past|future|waiting|rejected)$",
                    message = "Invalid parameter value.") String stateParam) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get bookings with state {}, userId={}", stateParam, userId);
		return bookingClient.getBookingsMadeByUser(userId, state);
	}

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "state", required = false, defaultValue = "all")
            @Pattern(regexp = "^(all|current|past|future|waiting|rejected)$",
                    message = "Invalid parameter value.") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get bookings of userId={} items with state {}", userId, stateParam);
        return bookingClient.getBookingsOfUserItems(userId, state);
    }

}