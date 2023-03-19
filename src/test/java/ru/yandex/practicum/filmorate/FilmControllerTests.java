package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class FilmControllerTests {

    @Autowired
    FilmController filmController;
    @Autowired
    FilmService filmService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearFilms() {
        filmService.findFilms().clear();
        filmService.setIdGenerator(0);
    }

    @SneakyThrows
    @Test
    void filmWithGoodBehaviorTest() {
        Film film = Film.builder()
                .name("Film")
                .description("New Film")
                .releaseDate(LocalDate.of(2021, 12, 15))
                .duration(200)
                .build();
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void filmCreateFailNameTest() {
        Film film = Film.builder()
                .name("")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1994, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void filmCreateFailDescriptionTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescriptionFilmDescriptionFilmDescriptionFilmDescriptionFilmDescription" +
                        "FilmDescriptionFilmDescriptionFilmDescriptionFilmDescriptionFilmDescription" +
                        "FilmDescriptionFilmDescriptionFilmDescriptionFilmDescriptionFilmDescription" +
                        "FilmDescriptionFilmDescriptionFilmDescriptionFilmDescriptionFilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1994, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void filmCreateFailRealiseDateTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1850, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void filmCreateFailDurationTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(-1)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void filmUpdateGoodBehaviorTest() {
        Film film1 = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film1)));
        Film film2 = Film.builder()
                .id(1L)
                .name("Name")
                .description("FilmDescription")
                .duration(140)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.duration").value(140))
                .andExpect(jsonPath("$.id").value(1));
    }

    @SneakyThrows
    @Test
    void filmUpdateBadBehaviorTest() {
        Film film1 = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film1)));
        Film film2 = Film.builder()
                .id(999L)
                .name("Name")
                .description("FilmDescription")
                .duration(140)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void filmGetAllTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(get("/films"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(filmService.findFilms())));
    }

    @SneakyThrows
    @Test
    void filmAddLikeAndItsOkTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011,10,7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().is(200))
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Лайк поставлен"));
    }

    @SneakyThrows
    @Test
    void filmRemoveLikeAndItsOkTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011,10,7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(put("/films/1/like/1"));
        mockMvc.perform(delete("/films/1/like/1"))
                .andExpect(status().is(200))
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string("Лайк удален"));
    }

    @SneakyThrows
    @Test
    void filmRemoveLikeBadBehaviorTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011,10,7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        mockMvc.perform(put("/films/1/like/1"));
        mockMvc.perform(delete("/films/1/like/-2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void getFilmByIdAndItsOkTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(get("/films/1"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(filmService.getFilmById(1L))));
    }

    @SneakyThrows
    @Test
    void getFilmByIdBadBehaviorTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(get("/films/999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void getPopularFilmAndItsOkTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(filmService.findFilms())));
    }

    @SneakyThrows
    @Test
    void getMostPopularFilmAndItsOkTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        User user = User.builder()
                .email("Name1@mail.ru")
                .name("Name1")
                .login("NickName1")
                .birthday(LocalDate.of(2011,10,7))
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        Film film1 = Film.builder()
                .name("Name1")
                .description("FilmDescription1")
                .duration(170)
                .releaseDate(LocalDate.of(2000, 10, 7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film1)));
        mockMvc.perform(put("/films/1/like/1"));
        mockMvc.perform(get("/films/popular?count=1"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(filmService.getFilmById(1L)))));
    }
}
