package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class CommentMapper {

    public static CommentResponseDto commentToDtoForResponse(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                comment.getAuthor().getName(),
                comment.getCreateDate()
        );
    }

    public static Comment dtoToCommentForCreate(CommentCreateDto commentCreateDto, Item item, User author) {
        return new Comment(
                commentCreateDto.getText(),
                item,
                author
        );
    }

}