package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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

    public List<Mpa> getMpaList() {
        String sql = "SELECT mp.mpa_id, mp.mpa_name FROM public.mpa_rating AS mp";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    public Mpa getMpaByMpaId(int mpaId) {
        String sql = "SELECT mp.mpa_id, mp.mpa_name FROM public.mpa_rating AS mp WHERE mpa_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, mpaId);
        if (sqlRowSet.next()) {
            return Mpa.builder()
                    .id(sqlRowSet.getInt("mpa_id"))
                    .name(sqlRowSet.getString("mpa_name"))
                    .build();
        } else {
            throw new ArgumentNotFoundException("Такого id жанра не существует");
        }
    }
}
