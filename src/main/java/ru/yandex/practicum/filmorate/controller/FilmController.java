package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    @Setter
    private int idGenerator = 0;
    @Getter
    private List<Film> films = new ArrayList<>();

    @GetMapping
    public List<Film> findAll() {
        log.debug("Доступно фильмов: {}",films.size());
        return films;
    }

    @PostMapping
    public Film create(@RequestBody @NonNull Film film) {
        if (film.getName().equals("")) {
            log.error("Имя фильма не может быть пустым");
            throw new ValidationException("Передано пустое имя");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание не может быть больше 200 символов");
            throw new ValidationException("Описание слишком длинное");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.error("Дата не может быть раньше 28.12.1985");
            throw new ValidationException("Некорректная дата");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность не может быть отрицательной");
            throw new ValidationException("Введена не правильная продолжительность");
        }
        int id = ++idGenerator;
        film.setId(id);
        films.add(film);
        log.info("Новый фильм был добавлен");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @NonNull Film film) {
        if (film.getName().equals("")) {
            log.error("Имя фильма не может быть пустым");
            throw new ValidationException("Передано пустое имя");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание не может быть больше 200 символов");
            throw new ValidationException("Описание слишком длинное");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.error("Дата не может быть раньше 28.12.1985");
            throw new ValidationException("Некорректная дата");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность не может быть отрицательной");
            throw new ValidationException("Введена не правильная продолжительность");
        }
        if (!films.contains(film)) {
            log.error("Такого фильма нет");
            throw new ValidationException("Нечего обновлять");
        }
        films.set(films.indexOf(film), film);
        log.info("Информация о фильме обновлена");
        return film;
    }
}

