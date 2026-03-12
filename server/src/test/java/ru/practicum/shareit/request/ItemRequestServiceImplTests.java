package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:h2:mem:shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTests {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void testAddItemRequest_Success() {
        UserDto userDto = new UserDto(null, "Олег", "some@email.com");
        userDto = userService.addUser(userDto);

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Ищу балалайку на один вечер");

        ItemRequestResponseDto result = itemRequestService.addItemRequest(userDto.getId(), itemRequestCreateDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), notNullValue());
        assertThat(result.getDescription(), equalTo(itemRequestCreateDto.getDescription()));
        assertThat(result.getCreated(), notNullValue());
        assertThat(result.getItems(), empty());
    }

    @Test
    void testAddItemRequest_UserNotFound_ThrowsException() {
        Long nonExistentUserId = 999L;
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Ищу балалайку на один вечер");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addItemRequest(nonExistentUserId, itemRequestCreateDto));

        assertThat(exception.getMessage(), containsString("Пользователь с ID: " + nonExistentUserId + " не найден"));
    }

    @Test
    void testGetRequestsWithInfo() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        UserDto userDto3 = new UserDto(null, "Ира", "ira@gmail.com");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);
        userDto3 = userService.addUser(userDto3);

        ItemRequestCreateDto itemRequestDto1 = new ItemRequestCreateDto("Ищу балалайку на один вечер");
        ItemRequestCreateDto itemRequestDto2 = new ItemRequestCreateDto("Ищу зарядку для аккумулятора авто");
        ItemRequestCreateDto itemRequestDto3 = new ItemRequestCreateDto("Ищу самовар");

        ItemRequestResponseDto itemRequestResponseDto1
                = itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto1);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println(">>>>> Ошибка при добавлении лага 100 мс <<<<<");
        }

        ItemRequestResponseDto itemRequestResponseDto2
                = itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto2);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println(">>>>> Ошибка при добавлении лага 100 мс <<<<<");
        }

        ItemRequestResponseDto itemRequestResponseDto3
                = itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto3);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println(">>>>> Ошибка при добавлении лага 100 мс <<<<<");
        }

        ItemDto itemForRequest1 = new ItemDto(null, "Балалайка", "На ней играл Элвис Пресли",
                true, itemRequestResponseDto1.getId());
        ItemDto itemForRequest2 = new ItemDto(null, "Самовар",
                "Использовался только один раз на свадьбе", true, itemRequestResponseDto3.getId());

        itemForRequest1 = itemService.addItem(userDto2.getId(), itemForRequest1);
        itemForRequest2 = itemService.addItem(userDto3.getId(), itemForRequest2);

        itemRequestResponseDto1.setItems(List.of(new ItemDtoForRequest(
                itemForRequest1.getId(),
                itemForRequest1.getName(),
                userDto2.getId()
        )));

        itemRequestResponseDto3.setItems(List.of(new ItemDtoForRequest(
                itemForRequest2.getId(),
                itemForRequest2.getName(),
                userDto3.getId()
        )));

        List<ItemRequestResponseDto> requests = itemRequestService.getRequestsWithInfo(userDto1.getId());

        assertThat(requests, allOf(notNullValue(), not(empty())));
        assertThat(requests, contains(itemRequestResponseDto3, itemRequestResponseDto2, itemRequestResponseDto1));
    }

    @Test
    void testGetRequestsWithInfo_NoRequests_ReturnsEmptyList() {
        UserDto userDto = new UserDto(null, "Олег", "some@email.com");
        userDto = userService.addUser(userDto);

        List<ItemRequestResponseDto> requests = itemRequestService.getRequestsWithInfo(userDto.getId());

        assertThat(requests, notNullValue());
        assertThat(requests, empty());
    }

    @Test
    void testGetRequestsWithInfo_UserHasRequestsWithoutItems() {
        UserDto userDto = new UserDto(null, "Олег", "some@email.com");
        userDto = userService.addUser(userDto);

        ItemRequestCreateDto itemRequestDto1 = new ItemRequestCreateDto("Ищу балалайку на один вечер");
        ItemRequestCreateDto itemRequestDto2 = new ItemRequestCreateDto("Ищу зарядку для аккумулятора авто");

        ItemRequestResponseDto responseDto1 = itemRequestService.addItemRequest(userDto.getId(), itemRequestDto1);
        ItemRequestResponseDto responseDto2 = itemRequestService.addItemRequest(userDto.getId(), itemRequestDto2);

        List<ItemRequestResponseDto> requests = itemRequestService.getRequestsWithInfo(userDto.getId());

        assertThat(requests, hasSize(2));
        assertThat(requests, contains(responseDto1, responseDto2));
        assertThat(requests.get(0).getItems(), empty());
        assertThat(requests.get(1).getItems(), empty());
    }

    @Test
    void testGetOtherUsersRequests_NoOtherUsersRequests_ReturnsEmptyList() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);

        ItemRequestCreateDto itemRequestDto = new ItemRequestCreateDto("Ищу балалайку на один вечер");
        itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto);

        List<ItemRequestResponseDto> requests = itemRequestService.getOtherUsersRequests(userDto1.getId());

        assertThat(requests, notNullValue());
        assertThat(requests, empty());
    }

    @Test
    void testGetOtherUsersRequests_WithOtherUsersRequests_ReturnsRequests() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);

        ItemRequestCreateDto itemRequestDto1 = new ItemRequestCreateDto("Ищу балалайку на один вечер");
        ItemRequestCreateDto itemRequestDto2 = new ItemRequestCreateDto("Ищу зарядку для аккумулятора авто");
        ItemRequestCreateDto itemRequestDto3 = new ItemRequestCreateDto("Ищу самовар");

        itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto1);
        itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto2);

        ItemRequestResponseDto user2Request = itemRequestService.addItemRequest(userDto2.getId(), itemRequestDto3);

        List<ItemRequestResponseDto> requests = itemRequestService.getOtherUsersRequests(userDto1.getId());

        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).getDescription(), equalTo(itemRequestDto3.getDescription()));
        assertThat(requests.get(0).getId(), equalTo(user2Request.getId()));
    }

    @Test
    void testGetOtherUsersRequests_SortedByCreatedDesc() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);

        ItemRequestCreateDto itemRequestDto1 = new ItemRequestCreateDto("Первый запрос");
        ItemRequestCreateDto itemRequestDto2 = new ItemRequestCreateDto("Второй запрос");
        ItemRequestCreateDto itemRequestDto3 = new ItemRequestCreateDto("Третий запрос");

        ItemRequestResponseDto responseDto1 = itemRequestService.addItemRequest(userDto2.getId(), itemRequestDto1);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println(">>>>> Ошибка при добавлении лага 100 мс <<<<<");
        }

        ItemRequestResponseDto responseDto2 = itemRequestService.addItemRequest(userDto2.getId(), itemRequestDto2);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println(">>>>> Ошибка при добавлении лага 100 мс <<<<<");
        }

        ItemRequestResponseDto responseDto3 = itemRequestService.addItemRequest(userDto2.getId(), itemRequestDto3);

        List<ItemRequestResponseDto> requests = itemRequestService.getOtherUsersRequests(userDto1.getId());

        assertThat(requests, hasSize(3));
        assertThat(requests, contains(responseDto3, responseDto2, responseDto1));
    }

    @Test
    void testGetRequestById_Success() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);

        ItemRequestCreateDto itemRequestDto = new ItemRequestCreateDto("Ищу балалайку на один вечер");
        ItemRequestResponseDto createdRequest = itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto);

        ItemDto itemForRequest = new ItemDto(null, "Балалайка", "На ней играл Элвис Пресли",
                true, createdRequest.getId());
        itemForRequest = itemService.addItem(userDto2.getId(), itemForRequest);

        ItemRequestResponseDto result = itemRequestService.getRequestById(userDto2.getId(), createdRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(createdRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.getItems(), hasSize(1));

        ItemDtoForRequest itemDto = result.getItems().get(0);
        assertThat(itemDto.getId(), equalTo(itemForRequest.getId()));
        assertThat(itemDto.getName(), equalTo(itemForRequest.getName()));
        assertThat(itemDto.getOwnerId(), equalTo(userDto2.getId()));
    }

    @Test
    void testGetRequestById_RequestNotFound_ThrowsException() {
        UserDto userDto = new UserDto(null, "Олег", "some@email.com");
        userDto = userService.addUser(userDto);

        Long nonExistentRequestId = 999L;
        Long userId = userDto.getId();

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(userId, nonExistentRequestId));

        assertThat(exception.getMessage(), containsString("Запрос с ID: " + nonExistentRequestId + " не найден"));
    }

    @Test
    void testGetRequestById_RequestWithoutItems_ReturnsRequestWithEmptyItems() {
        UserDto userDto1 = new UserDto(null, "Олег", "some@email.com");
        UserDto userDto2 = new UserDto(null, "Саша", "some@yandex.ru");
        userDto1 = userService.addUser(userDto1);
        userDto2 = userService.addUser(userDto2);

        ItemRequestCreateDto itemRequestDto = new ItemRequestCreateDto("Ищу балалайку на один вечер");
        ItemRequestResponseDto createdRequest = itemRequestService.addItemRequest(userDto1.getId(), itemRequestDto);

        ItemRequestResponseDto result = itemRequestService.getRequestById(userDto2.getId(), createdRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(createdRequest.getId()));
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(result.getItems(), empty());
    }

}