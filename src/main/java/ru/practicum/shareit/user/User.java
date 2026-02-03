package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;

    @NotNull(message = "E-mail не может быть null")
    @Email(message = "Указанный E-mail не соответствует формату")
    private String email;

    @NotBlank(message = "Имя не может быть null или пустым")
    private String name;
}