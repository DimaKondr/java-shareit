package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Описание не может быть null или пустым")
    @Size(max = 400, message = "Максимальная длина отзыва — 400 символов")
    @Column(name = "text", nullable = false, length = 400)
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @NotNull(message = "Вещь для отзыва не может быть null")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull(message = "Автор отзыва не может быть null")
    private User author;

    @Column(name = "created_date")
    private LocalDateTime createDate;

    public Comment(String text, Item item, User author) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.createDate = LocalDateTime.now(ZoneId.of("UTC"));
    }
}