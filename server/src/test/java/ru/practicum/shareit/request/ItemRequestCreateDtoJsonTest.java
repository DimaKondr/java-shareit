package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestCreateDtoJsonTest {
    private final JacksonTester<ItemRequestCreateDto> json;

    @Test
    void testItemRequestCreateDto() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Самовар");

        JsonContent<ItemRequestCreateDto> result = json.write(itemRequestCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Самовар");
    }

}