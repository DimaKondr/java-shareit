package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    // Добавляем новый запрос на вещь.
    ItemRequestResponseDto addItemRequest(Long requestorId, ItemRequestCreateDto itemRequestCreateDto);

    // Получаем список всех запросов вещей пользователя с данными об ответах.
    List<ItemRequestResponseDto> getRequestsWithInfo(Long requestorId);

    // Получаем список запросов, созданных другими пользователями.
    List<ItemRequestResponseDto> getOtherUsersRequests(Long requestorId);

    // Получаем запрос по ID.
    ItemRequestResponseDto getRequestById(Long requestorId, Long requestId);

}