package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Primary
public class FilmDBStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final MpaDao mpaDao;

    private final GenreDao genreDao;

    public FilmDBStorage(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "INSERT INTO public.films (film_name, film_description," +
                " film_duration, release_date) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setLong(3, film.getDuration());
            stmt.setObject(4, film.getReleaseDate());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        if (film.getMpa() != null) {
            jdbcTemplate.update("UPDATE public.films AS f SET f.mpa_id = ?" +
                    " WHERE f.film_id = ?", film.getMpa().getId(), film.getId());
        }
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update("INSERT INTO public.films_genre (film_id, genre_id) VALUES(?,?)", film.getId(),
                    genre.getId());
        }
        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmStorageValidation(film)) {
            if (film.getMpa() == null || film.getMpa().getId() == 0) {
                String sqlFilmUpdate = "UPDATE public.films SET film_name = ?, film_description = ?, " +
                        "film_duration = ?, release_date = ? WHERE film_id = ?";
                jdbcTemplate.update(sqlFilmUpdate, film.getName(), film.getDescription(), film.getDuration(),
                        film.getReleaseDate(), film.getId());
            } else {
                String sqlFilmUpdate = "UPDATE public.films SET film_name = ?, film_description = ?, " +
                        "film_duration = ?, release_date = ?, mpa_id = ? WHERE film_id = ?";
                jdbcTemplate.update(sqlFilmUpdate, film.getName(), film.getDescription(), film.getDuration(),
                        film.getReleaseDate(), film.getMpa().getId(), film.getId());
            }
            String sqlClearLikes = "DELETE FROM public.likes_to_film WHERE film_id = ?";
            jdbcTemplate.update(sqlClearLikes, film.getId());
            for (long likesId : film.getUserLikesIds()) {
                String sqlPutNewLikes = "INSERT INTO public.likes_to_film (user_id, film_id) VALUES(?, ?)";
                jdbcTemplate.update(sqlPutNewLikes, likesId, film.getId());
            }
            String sqlClearGenres = "DELETE FROM public.films_genre WHERE film_id = ?";
            jdbcTemplate.update(sqlClearGenres, film.getId());
            for (Genre genre : film.getGenres()) {
                String sqlPutNewGenres = "INSERT INTO public.films_genre (film_id, genre_id) VALUES(?,?)";
                jdbcTemplate.update(sqlPutNewGenres, film.getId(),
                        genre.getId());
            }
            return getFilmById(film.getId());
        } else {
            throw new ArgumentNotFoundException("Такого фильма нет");
        }
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * FROM public.films as f " +
                "LEFT JOIN mpa_rating AS mp ON f.mpa_id = mp.mpa_id";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film removeFilm(Film film) {
        if (filmStorageValidation(film)) {
            String sql = "DELETE FROM public.films WHERE film_id = ?";
            jdbcTemplate.update(sql, film.getId());
            return film;
        } else {
            throw new ArgumentNotFoundException("Такого фильма нет");
        }
    }

    @Override
    public Film getFilmById(Long id) {
        Film filmValid = Film.builder()
                .id(id)
                .build();
        if (filmStorageValidation(filmValid)) {
            String sql = "SELECT * FROM public.films as f " +
                    "LEFT JOIN mpa_rating AS mp ON f.mpa_id = mp.mpa_id WHERE f.film_id = ?";
            Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, id);
            return film;
        } else {
            throw new ArgumentNotFoundException("Такого фильма нет");
        }
    }

    public Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("film_name");
        String description = rs.getString("film_description");
        long duration = rs.getLong("film_duration");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Mpa mpaRating = mpaDao.makeMpa(rs);
        List<Genre> genres = genreDao.getGenreListByFilmId(id);
        List<Long> friends = jdbcTemplate.queryForList("SELECT user_id FROM public.likes_to_film WHERE film_id = ?",
                Long.class, id);
        Film film = Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .releaseDate(releaseDate)
                .mpa(mpaRating)
                .build();
        film.getGenres().addAll(genres);
        film.getUserLikesIds().addAll(friends);
        return film;
    }

    public boolean filmStorageValidation(Film film) {
        String sql = "SELECT COUNT(*) FROM public.films WHERE film_id = ?";
        Integer coincidence = jdbcTemplate.queryForObject(sql, Integer.class, film.getId());
        if (coincidence != 0) {
            return true;
        }
        return false;
    }
}
