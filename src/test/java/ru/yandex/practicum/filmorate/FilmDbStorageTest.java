package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmDbStorage.class,
        UserDbStorage.class
})
class FilmDbStorageTest {


    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;


    @Test
    void shouldCreateFilm() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmStorage.create(film);

        assertTrue(created.getId() > 0);
        assertEquals("Film", created.getName());
    }

    @Test
    void shouldFindFilmById() {

        Film film = new Film();
        film.setName("Avatar");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2009, 12, 18));
        film.setDuration(162);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmStorage.create(film);

        Optional<Film> found = filmStorage.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
        assertEquals("Avatar", found.get().getName());
    }

    @Test
    void shouldUpdateFilm() {

        Film film = new Film();
        film.setName("Old");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        Film created = filmStorage.create(film);

        created.setName("New");

        filmStorage.update(created);

        Film updated = filmStorage.getById(created.getId()).orElseThrow();

        assertEquals("New", updated.getName());
    }

    @Test
    void shouldReturnAllFilms() {

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        filmStorage.create(film);

        assertFalse(filmStorage.getAll().isEmpty());
    }

    @Test
    void shouldReturnPopularFilms() {

        Film film = new Film();
        film.setName("Popular");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "G"));

        filmStorage.create(film);

        List<Film> films = filmStorage.getPopular(10);

        assertFalse(films.isEmpty());
    }

    @Test
    void shouldAddLike() {

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));


        user = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));

        film = filmStorage.create(film);

        filmStorage.addLike(film.getId(), user.getId());

        List<Film> popular = filmStorage.getPopular(10);

        assertEquals(film.getId(), popular.get(0).getId());
    }

    @Test
    void shouldRemoveLike() {

        User user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));


        user = userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        film.setMpa(new Mpa(1, "G"));

        film = filmStorage.create(film);

        int filmId = film.getId();

        filmStorage.addLike(filmId, user.getId());
        filmStorage.removeLike(filmId, user.getId());

        List<Film> popular = filmStorage.getPopular(10);

        assertTrue(
                filmStorage.getById(filmId).isPresent()
        );
    }
}