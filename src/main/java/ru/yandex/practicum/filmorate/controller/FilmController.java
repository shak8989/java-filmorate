package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен запрос на получение списка фильмов");
        return filmService.getFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        return filmService.updateFilm(film);
    }

    private void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null &&
                film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза указана неверно");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительной");
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id,
                        @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopular(count);
    }
}