package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;
import java.util.LinkedHashSet;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;


@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;


    private void saveGenres(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film create(Film film) {
        String sql = """
                INSERT INTO films(name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            if (film.getMpa() != null) {
                statement.setInt(5, film.getMpa().getId());
            } else {
                statement.setNull(5, Types.INTEGER);
            }

            return statement;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());

        saveGenres(film);

        return getById(film.getId()).orElseThrow();
    }

    @Override
    public Film update(Film film) {
        String sql = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        jdbcTemplate.update(
                "DELETE FROM film_genres WHERE film_id=?",
                film.getId());

        saveGenres(film);

        return getById(film.getId()).orElseThrow();
    }

    private Set<Genre> loadGenres(Integer filmId) {

        String sql = """
                SELECT g.id,
                       g.name
                FROM genres g
                JOIN film_genres fg
                ON g.id = fg.genre_id
                WHERE fg.film_id = ?
                ORDER BY g.id
                """;

        List<Genre> genres = jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(
                        rs.getInt("id"),
                        rs.getString("name")
                ),
                filmId);

        return new LinkedHashSet<>(genres);
    }

    private Mpa loadMpa(Integer id) {

        String sql = """
                SELECT id,
                       name
                FROM mpa
                WHERE id = ?
                """;

        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) ->
                        new Mpa(
                                rs.getInt("id"),
                                rs.getString("name")
                        ),
                id);
    }


    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT * FROM films";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getById(Integer id) {
        String sql = "SELECT * FROM films WHERE id = ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);

        if (films.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(films.getFirst());
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = """
                SELECT f.*
                FROM films f
                LEFT JOIN likes l ON f.id = l.film_id
                GROUP BY f.id
                ORDER BY COUNT(l.user_id) DESC
                LIMIT ?
                """;

        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        int mpaId = rs.getInt("mpa_id");

        if (!rs.wasNull()) {
            film.setMpa(loadMpa(mpaId));
        }

        film.setGenres(loadGenres(film.getId()));

        return film;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sql = """
                INSERT INTO likes (film_id, user_id)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Integer filmId, Integer userId) {
        String sql = """
                DELETE FROM likes
                WHERE film_id = ?
                AND user_id = ?
                """;

        jdbcTemplate.update(sql, filmId, userId);
    }
}