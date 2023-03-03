package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Setter
    private int idGenerator = 0;
    @Getter
    private final List<User> users = new ArrayList<>();

    @GetMapping
    public List<User> findAll() {
        log.debug("Доступно фильмов: {}",users.size());
        return users;
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        if (isValidUser(user)) {
            int id = ++idGenerator;
            user.setId(id);
            users.add(user);
            log.info("Новый пользователь был добавлен");
            return user;
        }
        return null;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        if (isValidUser(user)) {
            if (!users.contains(user)) {
                log.warn("Такого пользователя не существует");
                throw new InvalidUpdateException("Нечего обновлять");
            }
            users.set(users.indexOf(user), user);
            log.info("Информация о пользователе обновлена");
            return user;
        }
        return null;
    }

    public boolean isValidUser(User user) {
        if (StringUtils.containsWhitespace(user.getLogin())) {
            log.warn("Логин пользователя не может быть пустым или содержать пробелы");
            throw new ValidationException("Передан некорректный Логин");
        }
        if (!StringUtils.hasLength(user.getName())) {
            user.setName(user.getLogin());
            log.info("Передано пустое имя, вместо имени будет использоваться логин");
        }
        return true;
    }
}
