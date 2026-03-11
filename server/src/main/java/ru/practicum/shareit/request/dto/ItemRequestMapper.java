package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.ItemRequest;

import java.util.ArrayList;

@Component
public class ItemRequestMapper {

    public static ItemRequestResponseDto requestToDtoForResponse(ItemRequest itemRequest) {
        return new ItemRequestResponseDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<ItemDtoForRequest>()
        );
    }

    public static ItemRequest dtoToRequestForCreate(ItemRequestCreateDto itemRequestCreateDto, Long requestorId) {
        return new ItemRequest(itemRequestCreateDto.getDescription(), requestorId);
    }

}