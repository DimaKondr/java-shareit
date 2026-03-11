package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
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

    @Column(name = "text", nullable = false, length = 400)
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createDate;

    public Comment(String text, Item item, User author) {
        this.text = text;
        this.item = item;
        this.author = author;
        this.createDate = LocalDateTime.now(ZoneId.of("UTC"));
    }
}