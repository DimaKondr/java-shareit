package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Имя не может быть null или пустым")
    @Size(max = 100, message = "Максимальная длина имени — 100 символов")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "E-mail не может быть null")
    @Email(message = "Указанный E-mail не соответствует формату")
    @Size(max = 254, message = "Максимальная длина email — 254 символа")
    @Column(name = "email", nullable = false, length = 254, unique = true)
    private String email;
}