package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUpdateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsValue(film)) {
            films.put(film.getId(), film);
            return film;
        } else {
            log.warn("Такого фильма нет");
            throw new InvalidUpdateException("Нечего обновлять");
        }
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film removeFilm(Film film) {
        if (films.containsValue(film)) {
            films.remove(film.getId());
            return film;
        } else {
            log.warn("Такого фильма нет");
            throw new ArgumentNotFoundException("Фильм не найден");
        }
    }


    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            log.warn("Фильма с таким id {} нет", id);
            throw new ArgumentNotFoundException(String
                    .format("Неверный id. Фильма с id: \"%s\" не существует.", id));
        }
    }
}
