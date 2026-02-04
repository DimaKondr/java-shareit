package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HttpHeaderNames;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItem(@RequestHeader(HttpHeaderNames.USER_ID) Long ownerId,
                           @RequestBody
                               @Validated(ItemDto.OnCreate.class) ItemDto itemDto) {
        return itemService.addItem(ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable("itemId")
                               @NotNull(message = "itemId не может быть null")
                               @Valid Long itemId) {
        return itemService.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HttpHeaderNames.USER_ID) Long ownerId,
                              @PathVariable("itemId")
                                  @NotNull(message = "itemId не может быть null")
                                  @Valid Long itemId,
                              @RequestBody
                                  @Validated(ItemDto.OnUpdate.class)
                                  /*@Valid*/ ItemDto updatedItemDto) {
        return itemService.updateItem(itemId, ownerId, updatedItemDto);
    }

    @DeleteMapping("/{itemId}")
    public ItemDto removeUser(@RequestHeader(HttpHeaderNames.USER_ID) Long ownerId,
                              @PathVariable("itemId")
                                  @NotNull(message = "itemId не может быть null")
                                  @Valid Long removedItemId) {
        return itemService.removeItem(ownerId, removedItemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(HttpHeaderNames.USER_ID) Long ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(name = "text") String text) {
        return itemService.searchItems(text);
    }

}