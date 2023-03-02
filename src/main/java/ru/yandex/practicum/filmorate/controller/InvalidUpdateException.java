package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InvalidUpdateException extends RuntimeException {


    public InvalidUpdateException() {
    }

    public InvalidUpdateException(String message) {
        super(message);
    }
}
