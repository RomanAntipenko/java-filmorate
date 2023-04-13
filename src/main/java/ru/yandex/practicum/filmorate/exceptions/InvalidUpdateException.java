package ru.yandex.practicum.filmorate.exceptions;

public class InvalidUpdateException extends RuntimeException {


    public InvalidUpdateException() {
    }

    public InvalidUpdateException(String message) {
        super(message);
    }
}
