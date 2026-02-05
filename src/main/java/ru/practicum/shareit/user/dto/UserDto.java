package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;

    @Email(message = "Указанный E-mail не соответствует формату")
    private String email;
    private String name;
}