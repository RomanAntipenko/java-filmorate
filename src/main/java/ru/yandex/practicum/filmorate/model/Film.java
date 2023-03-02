package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private String description;
    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;
    @EqualsAndHashCode.Exclude
    private long duration;
}
