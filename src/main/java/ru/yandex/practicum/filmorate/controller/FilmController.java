package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);

        film.setId(nextId++);
        films.put(film.getId(), film);

        log.info("Добавлен фильм {}", film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);

        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с id=" + film.getId() + " не найден");
        }

        films.put(film.getId(), film);

        log.info("Обновлен фильм {}", film);

        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null
                && film.getDescription().length() > 200) {
            log.error("Описание фильма длиннее 200 символов");
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Некорректная дата релиза");
            throw new ValidationException("Дата релиза указана неверно");
        }

        if (film.getDuration() <= 0) {
            log.error("Некорректная продолжительность");
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }
}