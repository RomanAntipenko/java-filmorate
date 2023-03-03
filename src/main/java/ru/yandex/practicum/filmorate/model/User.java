package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;
    @EqualsAndHashCode.Exclude
    @Email
    private String email;
    @EqualsAndHashCode.Exclude
    @NotBlank
    private String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
}
