package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ArgumentNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InvalidUpdateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(),user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsValue(user)) {
            users.put(user.getId(), user);
            return user;
        } else {
            log.warn("Такого пользователя нет");
            throw new InvalidUpdateException("Нечего обновлять");
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public User removeUser(User user) {
        if(users.containsValue(user)) {
            users.remove(user.getId());
            return user;
        } else {
            log.warn("Такого пользователя нет");
            throw new ArgumentNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            log.warn("Пользователя с таким id: {} нет", id);
            throw new ArgumentNotFoundException(String
                    .format("Неверный id. Пользователя с id: \"%s\" не существует.", id));
        }
    }

}
