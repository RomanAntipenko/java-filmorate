package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre makeGenre(ResultSet rs) throws SQLException {
        if (rs.getLong("genre_id") == 0 && rs.getString("genre_name") == null) {
            return null;
        }
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }


    public Genre getGenreByGenreId(int genreId) {
        if (isValidId(genreId)) {
            String sql = "SELECT genre_id, genre_name FROM public.genre WHERE genre_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
        } else {
            throw new ArgumentNotFoundException("Такого id жанра нет");
        }

    }

    public List<Genre> getGenreListByFilmId(long filmId) {
        String sql = "SELECT fg.genre_id, g.genre_name FROM public.films AS f " +
                "LEFT JOIN public.films_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN public.genre AS g ON fg.genre_id = g.genre_id WHERE f.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
        if (genres.contains(null)) {
            return Collections.emptyList();
        }
        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public List<Genre> getAllGenreList() {
        String sql = "SELECT genre_id, genre_name FROM public.genre";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    public boolean isValidId(int id) {
        String sql = "SELECT COUNT(*) FROM public.genre WHERE genre_id = ?";
        Integer coincidence = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (coincidence != 0) {
            return true;
        }
        return false;
    }
}
