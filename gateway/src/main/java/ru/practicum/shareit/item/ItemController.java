package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestBody
                                              @Validated(ItemRequestDto.OnCreate.class)
                                              ItemRequestDto requestDto) {
        log.info("Creating item by user {}", ownerId);
        return itemClient.addItem(ownerId, requestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable("itemId") @NotNull(message = "itemId can not be null")
                                              Long itemId) {
        log.info("Get item {}", itemId);
        return itemClient.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @PathVariable("itemId") @NotNull(message = "itemId can not be null")
                                                 Long itemId,
                                             @RequestBody @Validated(ItemRequestDto.OnUpdate.class)
                                                 ItemRequestDto updatedItemDto) {
        log.info("Update item {}", itemId);
        return itemClient.updateItem(itemId, ownerId, updatedItemDto);
    }

    @DeleteMapping("/{itemId}")
    public void removeItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                           @PathVariable("itemId")
                               @NotNull(message = "itemId can not be null")
                               Long removedItemId) {
        log.info("Remove item {}", removedItemId);
        itemClient.removeItem(ownerId, removedItemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get all items of user {}", ownerId);
        return itemClient.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "text") String text) {
        log.info("Search items by text");
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                             @RequestBody
                                                 @Valid CommentRequestDto requestDto,
                                             @PathVariable("itemId")
                                                 @NotNull(message = "itemId can not be null")
                                                 Long itemId) {
        log.info("Add comment by user {} to item {}", authorId, itemId);
        return itemClient.addComment(authorId, requestDto, itemId);
    }

}