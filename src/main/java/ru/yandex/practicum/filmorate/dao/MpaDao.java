package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public MpaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    public Mpa getMpaByFilmId(long filmId) {
        String sql = "SELECT mp.mpa_id, mp.mpa_name FROM public.films AS f LEFT JOIN public.mpa_rating AS mp ON f.mpa_id = mp.mpa_id WHERE f.film_id = ?";
        Mpa mpa = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), filmId);
        return mpa;
    }

    public List<Mpa> getMpaList() {
        String sql = "SELECT mp.mpa_id, mp.mpa_name FROM public.mpa_rating AS mp";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    public Mpa getMpaByMpaId(int mpaId) {
        if (isValidId(mpaId)) {
            String sql = "SELECT mp.mpa_id, mp.mpa_name FROM public.mpa_rating AS mp WHERE mpa_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), mpaId);
        } else {
            throw new ArgumentNotFoundException("Такого id жанра не существует");
        }
    }

    public boolean isValidId(int id) {
        String sql = "SELECT COUNT(*) FROM public.mpa_rating WHERE mpa_id = ?";
        Integer coincidence = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (coincidence != 0) {
            return true;
        }
        return false;
    }
}
