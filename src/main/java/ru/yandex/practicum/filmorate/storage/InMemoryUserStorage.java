package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUpdateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private List<User> users = new ArrayList<>();

    @Override
    public User addUser(User user) {
        users.add(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.contains(user)) {
            users.set(users.indexOf(user), user);
            return user;
        } else {
            log.warn("Такого пользователя нет");
            throw new InvalidUpdateException("Нечего обновлять");
        }
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public User removeUser(User user) {
        users.remove(user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = User.builder()
                .id(id)
                .build();
        if (users.contains(user)) {
            return users.get(users.indexOf(user));
        } else {
            log.warn("Пользователя с таким id: {} нет", id);
            throw new ArgumentNotFoundException(String
                    .format("Неверный id. Пользователя с id: \"%s\" не существует.", id));
        }
    }

}
