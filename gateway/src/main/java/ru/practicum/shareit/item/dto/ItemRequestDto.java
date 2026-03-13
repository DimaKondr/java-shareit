package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    public interface OnCreate {}

    public interface OnUpdate {}

    private Long id;

    @NotBlank(message = "Название не может быть null или пустым", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Название не может быть null или пустым", groups = OnCreate.class)
    @Size(max = 200, message = "Максимальная длина описания — 200 символов", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(message = "Статус доступности вещи не должен быть null", groups = OnCreate.class)
    private Boolean available;
    private Long requestId;

}