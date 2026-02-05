package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id;

    @NotNull(message = "ID владельца вещи не может быть null")
    private Long ownerId;

    @NotBlank(message = "Название не может быть null или пустым")
    private String name;

    @NotBlank(message = "Описание не может быть null или пустым")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Статус доступности вещи не должен быть null")
    private Boolean available;
}