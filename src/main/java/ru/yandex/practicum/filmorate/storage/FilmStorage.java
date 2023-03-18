package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Map<Long, Film> getFilms();

    Film removeFilm(Film film);

    Film getFilmById(Long id);
}
