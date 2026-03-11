package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(long ownerId, ItemRequestDto requestDto) {
        return post("", ownerId, requestDto);
    }

    public ResponseEntity<Object> getItemById(long itemId) {
        return get("/" + itemId, itemId);
    }

    public ResponseEntity<Object> updateItem(long itemId, long ownerId, ItemRequestDto requestDto) {
        return patch("/" + itemId, ownerId, requestDto);
    }

    public void removeItem(long userId, long itemId) {
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long authorId, CommentRequestDto requestDto, long itemId) {
        return post("/" + itemId + "/comment", authorId, requestDto);
    }

}