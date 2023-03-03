package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    public Film create(@RequestBody @Valid Film film) {
        if (isFilmValid(film)) {
            int id = ++idGenerator;
            film.setId(id);
            films.add(film);
            log.info("Новый фильм был добавлен");
            return film;
        }
        return null;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        if (isFilmValid(film)) {
            if (!films.contains(film)) {
                log.warn("Такого фильма нет");
                throw new InvalidUpdateException("Нечего обновлять");
            }
            films.set(films.indexOf(film), film);
            log.info("Информация о фильме обновлена");
            return film;
        }
        return null;
    }

    public boolean isFilmValid(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))) {
            log.warn("Дата не может быть раньше 28.12.1985");
            throw new ValidationException("Некорректная дата");
        }
        return true;
    }
}

