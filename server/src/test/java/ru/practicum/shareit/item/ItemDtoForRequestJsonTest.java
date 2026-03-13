package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoForRequestJsonTest {
    private final JacksonTester<ItemDtoForRequest> json;

    @Test
    void testItemDtoForRequest() throws Exception {
        ItemDtoForRequest itemDtoForRequest = new ItemDtoForRequest(1L, "Самовар", 3L);

        JsonContent<ItemDtoForRequest> result = json.write(itemDtoForRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Самовар");
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(3);
    }

}