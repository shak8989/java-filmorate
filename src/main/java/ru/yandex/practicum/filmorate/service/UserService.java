package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getAll());
    }

    public User createUser(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User updateUser(User user) {

        getUserOrThrow(user.getId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    public void addFriend(Integer userId, Integer friendId) {

        if (userId.equals(friendId)) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {

        if (userId.equals(friendId)) {
            throw new ValidationException("Пользователи должны быть разными");
        }

        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Integer userId) {

        User user = getUserOrThrow(userId);

        return user.getFriends().stream()
                .map(id -> userStorage.getById(id).orElse(null))
                .toList();
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {

        if (userId.equals(otherId)) {
            throw new ValidationException("Пользователи должны быть разными");
        }

        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(id -> userStorage.getById(id).orElse(null))
                .toList();
    }

    private User getUserOrThrow(Integer id) {
        return userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}