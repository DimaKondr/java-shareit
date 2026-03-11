package ru.practicum.shareit.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getMessage(),
                "ОШИБКА ВАЛИДАЦИИ ТЕЛА ЗАПРОСА: " + e.getMessage()
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        return new ErrorResponse(e.getMessage(),
                "НЕКОРРЕКТНЫЕ ПАРАМЕТРЫ."
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        return new ErrorResponse(e.getMessage(),
                "ИСКОМЫЙ ОБЪЕКТ НЕ НАЙДЕН."
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        return new ErrorResponse("Произошла непредвиденная ошибка.",
                "НЕПРЕДВИДЕННАЯ ОШИБКА: " + e.getMessage()
        );
    }

    @ExceptionHandler(DataBaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataBaseException(final DataBaseException e) {
        return new ErrorResponse(e.getMessage(),
                "ОШИБКА ПОЛУЧЕНИЯ ДАННЫХ ИЗ БАЗЫ ДАННЫХ: " + e.getMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        return new ErrorResponse("Internal server error",
                "ВНУТРЕННЯЯ ОШИБКА СЕРВЕРА: " + e.getMessage()
        );
    }

}