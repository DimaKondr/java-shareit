package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
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
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название не может быть null или пустым")
    @Size(max = 100, message = "Максимальная длина названия — 100 символов")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Описание не может быть null или пустым")
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @NotNull(message = "Статус доступности вещи не должен быть null")
    @Column(name = "available", nullable = false)
    private Boolean available;

    @NotNull(message = "ID владельца вещи не может быть null")
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "request_id", nullable = false)
    private Long requestId;
}