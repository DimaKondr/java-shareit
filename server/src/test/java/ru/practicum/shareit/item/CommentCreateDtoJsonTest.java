package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentCreateDtoJsonTest {
    private final JacksonTester<CommentCreateDto> json;

    @Test
    void testCommentCreateDto() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Отличный самовар");

        JsonContent<CommentCreateDto> result = json.write(commentCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличный самовар");
    }

}