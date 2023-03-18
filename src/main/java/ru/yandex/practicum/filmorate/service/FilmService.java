package ru.yandex.practicum.filmorate.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Setter
    private long idGenerator;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<Film> findFilms() {
        return new ArrayList<>(filmStorage.getFilms().values());
    }

    public Film createFilm(Film film) {
        long id = ++idGenerator;
        film.setId(id);
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmStorage.updateFilm(film);
        return film;
    }

    public Film deleteFilm(Film film) {
        filmStorage.removeFilm(film);
        return film;
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public String addLikeToFilm(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new ArgumentNotFoundException(String.format("Пользователя с id: \"%s\" не существует." +
                    " Не существующий пользователь не может ставить лайки ", userId));
        }
        if (film.getUserLikesIds().contains(userId)) {
            return String.format("Пользователь с id: \"%s\" уже поставил лайк фильму: \"%s\".", userId, film.getName());
        } else {
            film.getUserLikesIds().add(userId);
            return "Лайк поставлен";
        }
    }

    public String removeLikeFromFilm(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        if (!film.getUserLikesIds().contains(userId)) {
            throw new ArgumentNotFoundException(String
                    .format("Пользователь с id: \"%s\" не ставил лайк фильму: \"%s\". Или такого id не существует.",
                    userId, film.getName()));
        } else {
            film.getUserLikesIds().remove(userId);
            return "Лайк удален";
        }
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.getFilms().values().stream()
                .sorted((x1, x2) -> x2.getUserLikesIds().size() - x1.getUserLikesIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
