package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    public interface OnCreate {}

    public interface OnUpdate {}

    private Long id;

    @NotBlank(groups = OnCreate.class)
    private String name;

    @Email(message = "Указанный E-mail не соответствует формату", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(groups = OnCreate.class)
    private String email;

}