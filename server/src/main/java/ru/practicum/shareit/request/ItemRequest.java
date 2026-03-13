package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime created;

    @Column(name = "requestor_id", nullable = false)
    private Long requestorId;

    public ItemRequest(String description, Long requestorId) {
        this.description = description;
        this.created = LocalDateTime.now(ZoneId.of("UTC"));
        this.requestorId = requestorId;
    }
}