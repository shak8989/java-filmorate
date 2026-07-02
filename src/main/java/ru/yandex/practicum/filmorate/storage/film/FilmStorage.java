package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Optional<Film> getById(Integer id);

    List<Film> getPopular(int count);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);


}