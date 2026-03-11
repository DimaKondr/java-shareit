package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
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

}