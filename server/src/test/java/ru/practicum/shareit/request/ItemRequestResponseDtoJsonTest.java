package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestResponseDtoJsonTest {
    private final JacksonTester<ItemRequestResponseDto> json;

    @Test
    void testItemRequestResponseDto() throws Exception {
        ItemDtoForRequest itemDtoForRequest1 = new ItemDtoForRequest(44L, "Самовар на углях", 15L);
        ItemDtoForRequest itemDtoForRequest2 = new ItemDtoForRequest(57L, "Самовар электрический", 19L);
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto(
                1L,
                "Нужен самовар",
                LocalDateTime.of(2026, 1, 10, 12, 37,52),
                List.of(itemDtoForRequest1, itemDtoForRequest2)
        );

        JsonContent<ItemRequestResponseDto> result = json.write(itemRequestResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужен самовар");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.of(2026, 1, 10, 12, 37,52).toString());
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(44);
        assertThat(result).extractingJsonPathStringValue("$.items.[0].name").isEqualTo("Самовар на углях");
        assertThat(result).extractingJsonPathNumberValue("$.items.[0].ownerId").isEqualTo(15);
        assertThat(result).extractingJsonPathNumberValue("$.items.[1].id").isEqualTo(57);
        assertThat(result).extractingJsonPathStringValue("$.items.[1].name").isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathNumberValue("$.items.[1].ownerId").isEqualTo(19);
    }

}