package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentResponseDtoJsonTest {
    private final JacksonTester<CommentResponseDto> json;

    @Test
    void testCommentResponseDto() throws Exception {
        Item item = new Item(12L, "Самовар", "Самовар электрический", true, 4L, null);
        CommentResponseDto commentResponseDto = new CommentResponseDto(1L, "Отличный самовар", item, "Коля",
                LocalDateTime.of(2026, 1, 10, 12, 37,52)
        );

        JsonContent<CommentResponseDto> result = json.write(commentResponseDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличный самовар");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(12);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Самовар электрический");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.item.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Коля");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(LocalDateTime.of(2026, 1, 10, 12, 37,52).toString());
    }

}