package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateDto {

    @NotBlank(message = "Описание не может быть null или пустым")
    @Size(max = 400, message = "Максимальная длина отзыва — 400 символов")
    private String text;
}