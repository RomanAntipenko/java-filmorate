package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
public class FilmControllerTests {

    @Autowired
    FilmController filmController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearFilms() {
        filmController.getFilms().clear();
        filmController.setIdGenerator(0);
    }

    @SneakyThrows
    @Test
    void filmWithGoodBehaviorTest() {
        Film film = Film.builder()
                .name("Film")
                .description("New Film")
                .releaseDate(LocalDate.of(2021,12,15))
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
                .releaseDate(LocalDate.of(1994,10,7))
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
                .releaseDate(LocalDate.of(1994,10,7))
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
                .releaseDate(LocalDate.of(1850,10,7))
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
                .releaseDate(LocalDate.of(1950,10,7))
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
                .releaseDate(LocalDate.of(1950,10,7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film1)));
        int id = filmController.getFilms().get(0).getId();
        Film film2 = Film.builder()
                .id(id)
                .name("Name")
                .description("FilmDescription")
                .duration(140)
                .releaseDate(LocalDate.of(1950,10,7))
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
                .releaseDate(LocalDate.of(1950,10,7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film1)));
        Film film2 = Film.builder()
                .id(999)
                .name("Name")
                .description("FilmDescription")
                .duration(140)
                .releaseDate(LocalDate.of(1950,10,7))
                .build();
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(film2)))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> result.getResponse().getErrorMessage());
    }

    @SneakyThrows
    @Test
    void filmGetAllTest() {
        Film film = Film.builder()
                .name("Name")
                .description("FilmDescription")
                .duration(150)
                .releaseDate(LocalDate.of(1950,10,7))
                .build();
        mockMvc.perform(post("/films")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(film)));
        mockMvc.perform(get("/films"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays
                        .asList(filmController.getFilms().get(0)))));
    }
}
