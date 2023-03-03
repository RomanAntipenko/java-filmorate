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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
class UserControllerTests {

	@Autowired
	private UserController userController;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	void clearUsers() {
		userController.getUsers().clear();
		userController.setIdGenerator(0);
	}

	@SneakyThrows
	@Test
	void userWithGoodBehaviorTest() {
		User user = User.builder()
				.name("Name")
				.email("Name@mail.ru")
				.login("NickName")
				.birthday(LocalDate.of(1994,10,7))
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
				.birthday(LocalDate.of(1994,10,7))
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
				.birthday(LocalDate.of(1994,10,7))
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
				.birthday(LocalDate.of(2200,10,7))
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
				.birthday(LocalDate.of(2000,10,7))
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
				.birthday(LocalDate.of(2001,10,7))
				.build();
		mockMvc.perform(post("/users")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(user1)));
		int id = userController.getUsers().get(0).getId();
		User user2 = User.builder()
				.id(id)
				.email("Name@mail.ru")
				.name("Name")
				.login("NickNameNick")
				.birthday(LocalDate.of(2000,10,7))
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
				.birthday(LocalDate.of(2001,10,7))
				.build();
		mockMvc.perform(post("/users")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(user1)));
		User user2 = User.builder()
				.id(999)
				.email("Name@mail.ru")
				.name("Name")
				.login("NickNameNick")
				.birthday(LocalDate.of(2000,10,7))
				.build();
		mockMvc.perform(put("/users")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(user2)))
				.andExpect(status().is5xxServerError())
				.andExpect(result -> result.getResponse().getErrorMessage());
	}

	@SneakyThrows
	@Test
	void userGetAllTest() {
		User user = User.builder()
				.email("Name1@mail.ru")
				.name("Name1")
				.login("NickName1")
				.birthday(LocalDate.of(2011,10,7))
				.build();
		mockMvc.perform(post("/users")
				.contentType("application/json")
				.content(objectMapper.writeValueAsString(user)));
		mockMvc.perform(get("/users"))
				.andExpect(status().is(200))
				.andExpect(content().json(objectMapper.writeValueAsString(Arrays
						.asList(userController.getUsers().get(0)))));
	}
}
