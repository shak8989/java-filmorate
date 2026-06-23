package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Integer userId) {
        User user = getUserOrThrow(userId);

        return user.getFriends().stream()
                .map(userStorage::getById)
                .toList();
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(userStorage::getById)
                .toList();
    }

    private User getUserOrThrow(Integer id) {
        User user = userStorage.getById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return user;
    }
}