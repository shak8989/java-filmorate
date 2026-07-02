package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void shouldCreateUser() {

        User user = new User();

        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        assertNotNull(created.getId());
        assertEquals("Alex", created.getName());
    }

    @Test
    void shouldFindUserById() {

        User user = new User();

        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        Optional<User> found = userStorage.getById(created.getId());

        assertTrue(found.isPresent());

        assertEquals(created.getId(), found.get().getId());
        assertEquals(created.getEmail(), found.get().getEmail());
    }

    @Test
    void shouldUpdateUser() {

        User user = new User();

        user.setEmail("user@mail.ru");
        user.setLogin("login");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User created = userStorage.create(user);

        created.setName("Updated");

        userStorage.update(created);

        User updated = userStorage.getById(created.getId()).orElseThrow();

        assertEquals("Updated", updated.getName());
    }

    @Test
    void shouldReturnAllUsers() {

        User user = new User();

        user.setEmail("test@mail.ru");
        user.setLogin("login");
        user.setName("Alex");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.create(user);

        assertFalse(userStorage.getAll().isEmpty());
    }

    @Test
    void shouldAddFriend() {

        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        List<User> friends = userStorage.getFriends(user1.getId());

        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0).getId());
    }

    @Test
    void shouldRemoveFriend() {

        User user1 = new User();
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1");
        user1.setName("User1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("User2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);

        userStorage.addFriend(user1.getId(), user2.getId());

        userStorage.removeFriend(user1.getId(), user2.getId());

        assertTrue(userStorage.getFriends(user1.getId()).isEmpty());
    }

    @Test
    void shouldReturnCommonFriends() {

        User user1 = new User();
        user1.setEmail("u1@mail.ru");
        user1.setLogin("u1");
        user1.setName("U1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("u2@mail.ru");
        user2.setLogin("u2");
        user2.setName("U2");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        User user3 = new User();
        user3.setEmail("u3@mail.ru");
        user3.setLogin("u3");
        user3.setName("U3");
        user3.setBirthday(LocalDate.of(2000, 1, 1));

        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        user3 = userStorage.create(user3);

        userStorage.addFriend(user1.getId(), user3.getId());
        userStorage.addFriend(user2.getId(), user3.getId());

        List<User> common =
                userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, common.size());
        assertEquals(user3.getId(), common.get(0).getId());
    }


}