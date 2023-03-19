package ru.yandex.practicum.filmorate.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Setter
    private long idGenerator;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findUsers() {
        return new ArrayList<>(userStorage.getUsers().values());
    }

    public User createUser(User user) {
        long id = ++idGenerator;
        user.setId(id);
        userStorage.addUser(user);
        return user;
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User deleteUser(User user) {
        userStorage.removeUser(user);
        return user;
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public String addUserToFriends(Long id, Long friendsId) {
        User user = userStorage.getUserById(id);
        User user1 = userStorage.getUserById(friendsId);
        if (user.getFriendsIds().contains(friendsId)) {
            return String.format("Пользователь: \"%s\" уже имеет пользователя: \"%s\" в друзьях.",
                    user.getLogin(), user1.getLogin());
        } else {
            user.getFriendsIds().add(friendsId);
            user1.getFriendsIds().add(id);
            return String.format("Пользователь: \"%s\" добавлен в друзья", user1.getLogin());
        }
    }

    public String removeUserFromFriends(Long id, Long friendsId) {
        User user = userStorage.getUserById(id);
        User user1 = userStorage.getUserById(friendsId);
        if (!user.getFriendsIds().contains(friendsId)) {
            return String.format("Пользователя: \"%s\" нет у пользователя: \"%s\" в друзьях.",
                    user1.getLogin(), user.getLogin());
        } else {
            user1.getFriendsIds().remove(id);
            user.getFriendsIds().remove(friendsId);
            return String.format("Пользователь: \"%s\" удален из друзей", user1.getLogin());
        }
    }

    public List<User> findAllFriends(Long id) {
        User user = userStorage.getUserById(id);
        List<User> list = new ArrayList<>();
        for (Long ids : user.getFriendsIds()) {
            list.add(userStorage.getUserById(ids));
        }
        return list;
    }

    public List<User> findCommonFriendsToUser(Long id, Long otherId) {
        User user = userStorage.getUserById(id);
        User user1 = userStorage.getUserById(otherId);
            Set<Long> friends = new HashSet<>(user.getFriendsIds());
            friends.retainAll(user1.getFriendsIds());
            List<User> common = new ArrayList<>();
            for (Long ids : friends) {
                common.add(userStorage.getUserById(ids));
            }
            return common;
    }

}
