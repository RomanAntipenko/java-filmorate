package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private final MpaDao mpaDao;

    @SneakyThrows
    @Test
    void mpaGetAndItsOkTest() {
        mockMvc.perform(get("/mpa/1"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(mpaDao.getMpaByMpaId(1))));
    }

    @SneakyThrows
    @Test
    void mpaGetAllTest() {
        mockMvc.perform(get("/mpa"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(mpaDao.getMpaList())));
    }

    @SneakyThrows
    @Test
    void mpaGetBadBehaviorTest() {
        mockMvc.perform(get("/mpa/9999"))
                .andExpect(status().is(404))
                .andExpect(result -> result.getResponse().getErrorMessage());
    }
}
