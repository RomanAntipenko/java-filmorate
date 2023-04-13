package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findUsers() {
        return userStorage.getUsers();
    }

    public User createUser(User user) {
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

    public String addOLDUserToFriends(Long id, Long friendsId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendsId);

        if (user.getFriendsIds().contains(friendsId)) {
            return String.format("Пользователь: \"%s\" уже имеет пользователя: \"%s\" в друзьях.",
                    user.getLogin(), friend.getLogin());
        } else {
            user.getFriendsIds().add(friendsId);
            friend.getFriendsIds().add(id);
            return String.format("Пользователь: \"%s\" добавлен в друзья", friend.getLogin());
        }
    }

    public String addUserToFriends(Long id, Long friendsId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendsId);
        if (user.getFriendsIds().contains(friendsId)) {
            return String.format("Пользователь: \"%s\" уже имеет пользователя: \"%s\" в друзьях.",
                    user.getLogin(), friend.getLogin());
        } else if (friend.getFollowedFriendsIds().contains(id)) {
            user.getFriendsIds().add(friendsId);
            friend.getFriendsIds().add(id);
            friend.getFollowedFriendsIds().remove(id);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
            return String.format("Пользователи: \"%s\" и \"%s\" теперь друзья",
                    friend.getLogin(), user.getLogin());
        } else {
            user.getFollowedFriendsIds().add(friendsId);
            userStorage.updateUser(user);
            return String.format("Теперь пользователь: \"%s\" фолловит, \"%s\".",
                    user.getLogin(), friend.getLogin());
        }
    }

    public String removeUserFromFriends(Long id, Long friendsId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendsId);
        if (!user.getFriendsIds().contains(friendsId) &&
                !user.getFollowedFriendsIds().contains(friendsId)) {
            return String.format("Пользователя: \"%s\" нет у пользователя: \"%s\" в друзьях.",
                    friend.getLogin(), user.getLogin());
        } else if (user.getFollowedFriendsIds().contains(friendsId)) {
            user.getFollowedFriendsIds().remove(friendsId);
            userStorage.updateUser(user);
            return String.format("Пользователь: \"%s\" удален из подписок", friend.getLogin());
        } else {
            friend.getFriendsIds().remove(id);
            friend.getFollowedFriendsIds().add(id);
            user.getFriendsIds().remove(friendsId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
            return String.format("Пользователь: \"%s\" удален из друзей. " +
                    "И теперь пользователь \"%s\" подписан на вас", friend.getLogin(), friend.getLogin());
        }
    }

    public List<User> findAllFriends(Long id) {
        User user = userStorage.getUserById(id);
        List<User> list = new ArrayList<>();
        Set<Long> idsOfFriendsAndFollows = new HashSet<>(user.getFriendsIds());
        idsOfFriendsAndFollows.addAll(user.getFollowedFriendsIds());
        for (Long ids : idsOfFriendsAndFollows) {
            list.add(userStorage.getUserById(ids));
        }
        return list;
    }

    /*public List<User> oldFindCommonFriendsToUser(Long id, Long otherId) {
        User user = userStorage.getUserById(id);
        User user1 = userStorage.getUserById(otherId);
            Set<Long> friends = new HashSet<>(user.getFriendsIds());
            friends.retainAll(user1.getFriendsIds());
            List<User> common = new ArrayList<>();
            for (Long ids : friends) {
                common.add(userStorage.getUserById(ids));
            }
            return common;
    }*/

    public List<User> findCommonFriendsToUser(Long id, Long otherId) {
        List<User> friendsToUser = findAllFriends(id);
        List<User> friendsToUser1 = findAllFriends(otherId);
        return friendsToUser.stream()
                .filter(friendsToUser1::contains)
                .distinct()
                .collect(Collectors.toList());
    }
}
