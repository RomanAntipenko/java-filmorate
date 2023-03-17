package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUpdateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private List<Film> films = new ArrayList<>();

    @Override
    public Film addFilm(Film film) {
            films.add(film);
            return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.contains(film)) {
            films.set(films.indexOf(film), film);
            return film;
        } else {
            log.warn("Такого фильма нет");
            throw new InvalidUpdateException("Нечего обновлять");
        }
    }

    @Override
    public List<Film> getFilms() {
        return films;
    }

    @Override
    public Film removeFilm(Film film) {
        films.remove(film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = Film.builder()
                .id(id)
                .build();
        if (films.contains(film)) {
            return films.get(films.indexOf(film));
        } else {
            log.warn("Фильма с таким id {} нет", id);
            throw new ArgumentNotFoundException(String
                    .format("Неверный id. Фильма с id: \"%s\" не существует.", id));
        }
    }
}
