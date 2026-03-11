package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                 @RequestBody @NotNull(message = "itemRequest не может быть null")
                                                     @Valid ItemRequestRequestDto requestDto) {
        log.info("Creating itemRequest {}, userId={}", requestDto, requestorId);
        return itemRequestClient.addItemRequest(requestorId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsWithInfo(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get itemRequests of user {}", requestorId);
        return itemRequestClient.getRequestsWithInfo(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        log.info("Get itemRequests of other users (excluding user {})", requestorId);
        return itemRequestClient.getOtherUsersRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                                 @PathVariable("requestId")
                                                     @NotNull(message = "requestId не может быть null")
                                                     Long requestId) {
        log.info("Get itemRequest {})", requestId);
        return itemRequestClient.getRequestById(requestorId, requestId);
    }

}