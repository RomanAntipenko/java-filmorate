package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

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
    public User create(@RequestBody @NonNull User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Email пользователя не может быть пустым");
            throw new ValidationException("Передан некорректный Email");
        }
        if (user.getLogin().contains(" ") || user.getLogin() == null) {
            log.error("Логин пользователя не может быть пустым или содержать пробелы");
            throw new ValidationException("Передан некорректный Логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть позже чем сейчас");
            throw new ValidationException("Некорректная дата");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Передано пустое имя, вместо имени будет использоваться логин");
        }
        int id = ++idGenerator;
        user.setId(id);
        users.add(user);
        log.info("Новый пользователь был добавлен");
        return user;
    }

    @PutMapping
    public User update(@RequestBody @NonNull User user) {
        //
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Email пользователя не может быть пустым");
            throw new ValidationException("Передан некорректный Email");
        }
        if (user.getLogin().contains(" ") || user.getLogin() == null) {
            log.error("Логин пользователя не может быть пустым или содержать пробелы");
            throw new ValidationException("Передан некорректный Логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть позже чем сейчас");
            throw new ValidationException("Некорректная дата");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
            log.info("Передано пустое имя, вместо имени будет использоваться логин");
        }
        if (!users.contains(user)) {
            log.error("Такого пользователя не существует");
            throw new InvalidUpdateException("Нечего обновлять");
        }
        users.set(users.indexOf(user),user);
        log.info("Информация о пользователе обновлена");
        return user;
    }
}
