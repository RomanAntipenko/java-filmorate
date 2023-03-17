package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InvalidUpdateException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    public void resetId() {
        userService.setIdGenerator(0);
    }
    @GetMapping
    public List<User> findAll() {
        log.debug("Доступно фильмов: {}",userService.findUsers().size());
        return userService.findUsers();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        if (isValidUser(user)) {
            userService.createUser(user);
            log.info("Новый пользователь был добавлен");
            return user;
        }
        return null;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        if (isValidUser(user)) {
            userService.updateUser(user);
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

    @PutMapping("{id}/friends/{friendsId}")
    public String addToFriends(@PathVariable("id") Long id,
                             @PathVariable("friendsId") Long friendsId) {
        return userService.addUserToFriends(id, friendsId);
    }

    @DeleteMapping("{id}/friends/{friendsId}")
    public String removeFromFriends(@PathVariable("id") Long id,
                               @PathVariable("friendsId") Long friendsId) {
        return userService.removeUserFromFriends(id, friendsId);
    }

    @GetMapping("{id}/friends")
    public List<User> findFriends(@PathVariable("id") Long id) {
        return userService.findAllFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable("id") Long id,
                                  @PathVariable("otherId") Long otherId) {
        return userService.findCommonFriendsToUser(id, otherId);
    }

    @GetMapping("{id}")
    public User findUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }
}
