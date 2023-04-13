package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDBStorage;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTests {

    @Autowired
    private final UserDBStorage userDBStorage;
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearUsers() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS PUBLIC.FRIENDS");
        jdbcTemplate.execute("DROP TABLE IF EXISTS PUBLIC.LIKES_TO_FILM");
        jdbcTemplate.execute("DROP TABLE IF EXISTS PUBLIC.USERS");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS PUBLIC.USERS (\n" +
                "\tUSER_ID INTEGER GENERATED BY DEFAULT AS IDENTITY,\n" +
                "\tUSER_NAME VARCHAR_IGNORECASE(50),\n" +
                "\tUSER_LOGIN VARCHAR_IGNORECASE(15) NOT NULL,\n" +
                "\tUSER_EMAIL VARCHAR_IGNORECASE(50) NOT NULL,\n" +
                "\tUSER_BIRTHDAY DATE NOT NULL,\n" +
                "\tCONSTRAINT USERS_PK PRIMARY KEY (USER_ID)\n" +
                ");");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS PUBLIC.FRIENDS (\n" +
                "\tUSER_ID INTEGER NOT NULL,\n" +
                "\tFRIENDS_ID INTEGER NOT NULL,\n" +
                "\tFRIENDS_STATUS VARCHAR_IGNORECASE,\n" +
                "\tCONSTRAINT FRIENDS_FK_1 FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "\tCONSTRAINT FRIENDS_FK_2 FOREIGN KEY (FRIENDS_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT\n" +
                ")");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS PUBLIC.LIKES_TO_FILM (\n" +
                "\tUSER_ID INTEGER,\n" +
                "\tFILM_ID INTEGER,\n" +
                "\tCONSTRAINT LIKES_TO_FILM_FK_FILM FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                "\tCONSTRAINT LIKES_TO_FILM_FK_USER FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                ")");
    }

    @SneakyThrows
    @Test
    void userWithGoodBehaviorTest() {
        User user = User.builder()
                .name("Name")
                .email("Name@mail.ru")
                .login("NickName")
                .birthday(LocalDate.of(1994, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void userCreateFailLoginTest() {
        User user = User.builder()
                .name("Name")
                .email("Name@mail.ru")
                .login("NickName Nick")
                .birthday(LocalDate.of(1994, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void userCreateFailEmailTest() {
        User user = User.builder()
                .name("Name")
                .email("Name@.ru")
                .login("NickNameNick")
                .birthday(LocalDate.of(1994, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void userCreateFailBirthdayTest() {
        User user = User.builder()
                .name("Name")
                .email("Name@mail.ru")
                .login("NickNameNick")
                .birthday(LocalDate.of(2200, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void userCreateWithEmptyNameTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value("NickNameNick"));
    }

    @SneakyThrows
    @Test
    void userUpdateGoodBehaviorTest() {
        User user1 = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user1)));
        User user2 = User.builder()
                .id(1L)
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.login").value("NickNameNick"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @SneakyThrows
    @Test
    void userUpdateBadBehaviorTest() {
        User user1 = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user1)));
        User user2 = User.builder()
                .id(999L)
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(put("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isNotFound())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void userGetAllTest() {
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(get("/users"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(userDBStorage.getUsers())));
    }

    @SneakyThrows
    @Test
    void getUserByIdAndItsOkTest() {
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(userDBStorage.getUserById(1L))));
    }

    @SneakyThrows
    @Test
    void getUserByIdBadBehaviorTest() {
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void userAddToFriendsAndItsOkTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        User friend = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(friend)));
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().is(200))
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @SneakyThrows
    @Test
    void userAddToFriendsBadBehaviorTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(put("/users/1/friends/999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void userRemoveFromFriendsAndItsOkTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        User friend = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(friend)));
        mockMvc.perform(put("/users/1/friends/2"));
        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().is(200))
                .andExpect(content().contentType("text/plain;charset=UTF-8"));
    }

    @SneakyThrows
    @Test
    void userGetEmptyCommonFriendsTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        User friend = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(friend)));
        mockMvc.perform(put("/users/1/friends/2"));
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @SneakyThrows
    @Test
    void userGetNotEmptyCommonFriendsTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        User friend = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(friend)));
        User friend1 = User.builder()
                .email("Common@mail.ru")
                .name("Common")
                .login("CommonNick")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(friend1)));
        mockMvc.perform(put("/users/1/friends/2"));
        mockMvc.perform(put("/users/1/friends/3"));
        mockMvc.perform(put("/users/2/friends/3"));
        mockMvc.perform(get("/users/2/friends/common/1"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDBStorage.getUserById(3L)))));
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDBStorage.getUserById(3L)))));
    }

    @SneakyThrows
    @Test
    void userGetFriendsOfUserTest() {
        User user = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickName")
                .birthday(LocalDate.of(2001, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        User friend = User.builder()
                .email("Name@mail.ru")
                .name("Name")
                .login("NickNameNick")
                .birthday(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(friend)));
        mockMvc.perform(put("/users/1/friends/2"));
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDBStorage.getUserById(2L)))));
    }
}
