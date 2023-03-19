package ru.yandex.practicum.filmorate.exceptions;

public class ArgumentNotFoundException extends RuntimeException {
    public ArgumentNotFoundException() {
    }

    public ArgumentNotFoundException(String message) {
        super(message);
    }
}
