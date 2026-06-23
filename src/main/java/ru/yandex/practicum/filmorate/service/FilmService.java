package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {

        Film film = filmStorage.getById(filmId);

        film.getLikes().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {

        Film film = filmStorage.getById(filmId);

        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int count) {

        return filmStorage.getAll().stream()
                .sorted(
                        Comparator.comparingInt(
                                (Film film) -> film.getLikes().size()
                        ).reversed()
                )
                .limit(count)
                .toList();
    }
}