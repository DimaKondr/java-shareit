package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestRequestDto {

    @NotBlank(message = "Описание запроса вещи не может быть null или пустым")
    @Size(max = 200, message = "Максимальная длина описания запроса — 200 символов")
    private String description;
}