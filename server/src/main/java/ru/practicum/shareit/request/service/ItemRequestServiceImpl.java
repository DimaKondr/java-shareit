package ru.practicum.shareit.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto addItemRequest(Long requestorId, ItemRequestCreateDto itemRequestCreateDto) {
        User requestor = userRepository.findById(requestorId).orElseThrow(() -> {
            log.error("Ошибка. Пользователь с ID: {} не найден.", requestorId);
            return new NotFoundException("Добавление запроса на вещь. " +
                    "Пользователь с ID: " + requestorId + " не найден");
        });
        ItemRequest addedItemRequest = ItemRequestMapper.dtoToRequestForCreate(itemRequestCreateDto, requestorId);
        addedItemRequest = itemRequestRepository.save(addedItemRequest);
        return ItemRequestMapper.requestToDtoForResponse(addedItemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getRequestsWithInfo(Long requestorId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);
        List<Long> requestId = new ArrayList<>();

        for (ItemRequest request : requests) {
            requestId.add(request.getId());
        }

        List<Item> itemsForRequest = itemRepository.findAllByRequestIdIn(requestId);
        List<ItemRequestResponseDto> result = new ArrayList<>();

        for (ItemRequest itemRequest : requests) {
            ItemRequestResponseDto requestDto = ItemRequestMapper.requestToDtoForResponse(itemRequest);
            for (Item item : itemsForRequest) {
                if (itemRequest.getId().equals(item.getRequestId())) {
                    ItemDtoForRequest itemDto = ItemMapper.itemToDtoForRequest(item);
                    requestDto.getItems().add(itemDto);
                }
            }
            result.add(requestDto);
        }
        return result;
    }

    @Override
    public List<ItemRequestResponseDto> getOtherUsersRequests(Long requestorId) {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(requestorId);
        List<ItemRequestResponseDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : requests) {
            ItemRequestResponseDto resultDto = ItemRequestMapper.requestToDtoForResponse(itemRequest);
            result.add(resultDto);
        }
        return result;
    }

    @Override
    public ItemRequestResponseDto getRequestById(Long requestorId, Long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(() -> {
            log.error("Ошибка. Запрос с ID: {} не найден.", requestId);
            return new NotFoundException("Получение запроса по ID. " +
                    "Запрос с ID: " + requestId + " не найден");
        });

        List<Item> itemsForRequest = itemRepository.findAllByRequestIdIn(List.of(request.getId()));
        ItemRequestResponseDto resultDto = ItemRequestMapper.requestToDtoForResponse(request);

        for (Item item : itemsForRequest) {
            ItemDtoForRequest itemDto = ItemMapper.itemToDtoForRequest(item);
            resultDto.getItems().add(itemDto);
        }
        return resultDto;
    }

}