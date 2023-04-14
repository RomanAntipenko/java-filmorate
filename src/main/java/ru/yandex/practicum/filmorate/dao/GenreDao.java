package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
        String sql = "SELECT genre_id, genre_name FROM public.genre WHERE genre_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, genreId);
        if (sqlRowSet.next()) {
            return Genre.builder()
                    .id(sqlRowSet.getInt("genre_id"))
                    .name(sqlRowSet.getString("genre_name"))
                    .build();
        } else {
            throw new ArgumentNotFoundException("Такого id жанра нет");
        }
    }

    public List<Genre> getGenreListByFilmId(long filmId) {
        String sql = "SELECT fg.genre_id, g.genre_name FROM public.films_genre AS fg " +
                "LEFT JOIN public.genre AS g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
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
}
