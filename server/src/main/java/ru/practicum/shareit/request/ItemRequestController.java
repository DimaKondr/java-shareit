package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HttpHeaderNames;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestResponseDto addItemRequest(@RequestHeader(HttpHeaderNames.USER_ID) Long requestorId,
                                                 @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        return itemRequestService.addItemRequest(requestorId, itemRequestCreateDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getRequestsWithInfo(@RequestHeader(HttpHeaderNames.USER_ID) Long requestorId) {
        return itemRequestService.getRequestsWithInfo(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getOtherUsersRequests(@RequestHeader(HttpHeaderNames.USER_ID)
                                                                  Long requestorId) {
        return itemRequestService.getOtherUsersRequests(requestorId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getRequestById(@RequestHeader(HttpHeaderNames.USER_ID) Long requestorId,
                                                 @PathVariable("requestId") Long requestId) {
        return itemRequestService.getRequestById(requestorId, requestId);
    }

}