package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Mpa> getAll() {
        String sql = "SELECT * FROM mpa ORDER BY id";

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Mpa(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
    }

    @Override
    public Optional<Mpa> getById(Integer id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";

        List<Mpa> mpaList = jdbcTemplate.query(sql,
                (rs, rowNum) ->
                        new Mpa(
                                rs.getInt("id"),
                                rs.getString("name")
                        ),
                id);

        return mpaList.stream().findFirst();
    }
}