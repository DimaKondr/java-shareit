package ru.practicum.shareit.error;

public class DataBaseException extends RuntimeException {
    public DataBaseException(String message) {
        super(message);
    }
}