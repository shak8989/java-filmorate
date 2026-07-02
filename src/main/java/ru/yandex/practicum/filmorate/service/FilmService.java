package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {
    private final MpaStorage mpaStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaStorage mpaStorage,
                       GenreStorage genreStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    private void validateGenres(Film film) {

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        for (Genre genre : film.getGenres()) {
            genreStorage.getById(genre.getId())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Жанр с id=" + genre.getId() + " не найден"));
        }
    }

    public List<Film> getFilms() {
        return new ArrayList<>(filmStorage.getAll());
    }

    public Film createFilm(Film film) {

        if (film.getMpa() != null) {
            mpaStorage.getById(film.getMpa().getId())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Рейтинг с id=" + film.getMpa().getId() + " не найден"));
        }

        validateGenres(film);

        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {

        getFilmOrThrow(film.getId());

        if (film.getMpa() != null) {
            mpaStorage.getById(film.getMpa().getId())
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Рейтинг с id=" + film.getMpa().getId() + " не найден"));
        }

        validateGenres(film);

        return filmStorage.update(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {

        if (count <= 0) {
            throw new ValidationException("Количество должно быть больше нуля");
        }

        return filmStorage.getPopular(count);
    }

    private Film getFilmOrThrow(Integer id) {
        return filmStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id=" + id + " не найден"));
    }

    private User getUserOrThrow(Integer id) {
        return userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id=" + id + " не найден"));
    }
}