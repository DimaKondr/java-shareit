package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;

    @NotBlank(message = "Описание не может быть null или пустым")
    @Size(max = 400, message = "Максимальная длина отзыва — 400 символов")
    private String text;

    @NotNull(message = "Вещь для отзыва не может быть null")
    private Item item;

    @NotNull(message = "Имя автора отзыва не может быть null")
    private String authorName;

    @NotNull(message = "Время создания отзыва не может быть null")
    private LocalDateTime created;
}