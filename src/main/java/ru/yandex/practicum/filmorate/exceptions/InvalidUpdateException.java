package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidUpdateException extends RuntimeException {


    public InvalidUpdateException() {
    }

    public InvalidUpdateException(String message) {
        super(message);
    }
}
