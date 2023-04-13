package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public List<Film> findFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        filmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
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
        User user = userStorage.getUserById(userId);
        if (film.getUserLikesIds().contains(userId)) {
            return String.format("Пользователь с id: \"%s\" уже поставил лайк фильму: \"%s\".", userId, film.getName());
        } else {
            film.getUserLikesIds().add(userId);
            filmStorage.updateFilm(film);
            return "Лайк поставлен";
        }
    }

    public String removeLikeFromFilm(Long id, Long userId) {
        Film film = filmStorage.getFilmById(id);
        User user = userStorage.getUserById(userId);
        if (!film.getUserLikesIds().contains(userId)) {
            throw new ArgumentNotFoundException(String
                    .format("Пользователь с id: \"%s\" не ставил лайк фильму: \"%s\". Или такого id не существует.",
                            userId, film.getName()));
        } else {
            film.getUserLikesIds().remove(userId);
            filmStorage.updateFilm(film);
            return "Лайк удален";
        }
    }

    public List<Film> findPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted((x1, x2) -> x2.getUserLikesIds().size() - x1.getUserLikesIds().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
