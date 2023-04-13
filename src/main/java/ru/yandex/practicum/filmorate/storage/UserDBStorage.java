package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Primary
public class UserDBStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUserById(Long id) {
        User userValid = User.builder()
                .id(id)
                .build();
        if (userStorageValidation(userValid)) {
            String sql = "SELECT * FROM public.users where user_id = ?";
            /*return jdbcTemplate.queryForObject(sql, (rs, rowNum)-> makeUser(rs), id);*/
            User user = jdbcTemplate.queryForObject(sql, this::makeUser, id);
            String sql1 = "SELECT friends_id FROM public.friends where user_id = ? AND friends_status = ?";
            List<Long> friends = jdbcTemplate.queryForList(sql1, Long.class, id, "ДРУЖБА");
            user.getFriendsIds().addAll(friends);
            List<Long> follows = jdbcTemplate.queryForList(sql1, Long.class, id, "ПОДПИСКА");
            user.getFollowedFriendsIds().addAll(follows);
            return user;
        } else {
            throw new ArgumentNotFoundException("Такого пользователя нет");
        }
    }

    public User makeUser(ResultSet rs, int rowNum) throws SQLException {
        long id = rs.getLong("user_id");
        String name = rs.getString("user_name");
        String login = rs.getString("user_login");
        String email = rs.getString("user_email");
        LocalDate birthday = rs.getDate("user_birthday").toLocalDate();
        User user = User.builder()
                .id(id)
                .name(name)
                .birthday(birthday)
                .login(login)
                .email(email)
                .build();
        return user;
    }

    @Override
    public User addUser(User user) {
        String sqlQuery = "INSERT INTO public.users (user_name, user_login," +
                " user_email, user_birthday) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getEmail());
            stmt.setObject(4, user.getBirthday());
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        if (userStorageValidation(user)) {
            String sqlUsers = "UPDATE public.users SET user_name = ?, user_login = ?, " +
                    "user_email = ?, user_birthday = ? WHERE user_id = ?";
            jdbcTemplate.update(sqlUsers, user.getName(), user.getLogin(), user.getEmail(),
                    user.getBirthday(), user.getId());
            String sqlClear = "DELETE FROM public.friends WHERE user_id = ?";
            jdbcTemplate.update(sqlClear, user.getId());
            String sqlFriends = "INSERT INTO public.friends (user_id, friends_id," +
                    " friends_status) VALUES(?, ?, ?)";
            for (Long ids : user.getFriendsIds()) {
                jdbcTemplate.update(sqlFriends, user.getId(), ids, "ДРУЖБА");
            }
            for (Long ids : user.getFollowedFriendsIds()) {
                jdbcTemplate.update(sqlFriends, user.getId(), ids, "ПОДПИСКА");
            }
            return getUserById(user.getId());
        } else {
            throw new ArgumentNotFoundException("Такого пользователя нет");
        }
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT * FROM public.users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User removeUser(User user) {
        if (userStorageValidation(user)) {
            String sql = "DELETE FROM public.users WHERE user_id= ?";
            jdbcTemplate.update(sql, user.getId());
            return user;
        } else {
            throw new ArgumentNotFoundException("Такого пользователя нет");
        }
    }

    public boolean userStorageValidation(User user) {
        String sql = "SELECT COUNT(*) FROM public.users WHERE user_id = ?";
        Integer coincidence = jdbcTemplate.queryForObject(sql, Integer.class, user.getId());
        if (coincidence != 0) {
            return true;
        }
        return false;
    }

}
