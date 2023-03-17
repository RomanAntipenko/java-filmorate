package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Доступно фильмов: {}", filmService.findFilms().size());
        return filmService.findFilms();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        if (isFilmValid(film)) {
            filmService.createFilm(film);
            log.info("Новый фильм был добавлен");
            return film;
        }
        return null;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        if (isFilmValid(film)) {
            filmService.updateFilm(film);
            log.info("Информация о фильме обновлена");
            return film;
        }
        return null;
    }

    public boolean isFilmValid(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата не может быть раньше 28.12.1985");
            throw new ValidationException("Некорректная дата");
        }
        return true;
    }

    @PutMapping("{id}/like/{userId}")
    public String addLike(@PathVariable("id") Long id,
                          @PathVariable("userId") Long userId) {
        return filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public String removeLike(@PathVariable("id") Long id,
                             @PathVariable("userId") Long userId) {
        return filmService.removeLikeFromFilm(id, userId);
    }

    @GetMapping("popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10", required = false)
                                 @PathVariable("count") int count) {
        return filmService.findPopularFilms(count);
    }

    @GetMapping("{id}")
    public Film findFilm(@PathVariable("id") Long id) {
        return filmService.getFilmById(id);
    }
}

