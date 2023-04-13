package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    @EqualsAndHashCode.Exclude
    @NotBlank
    private String name;
    @EqualsAndHashCode.Exclude
    @NotBlank
    @Size(min = 1, max = 200)
    private String description;
    @EqualsAndHashCode.Exclude
    @NotNull
    private LocalDate releaseDate;
    @EqualsAndHashCode.Exclude
    @Min(0)
    private long duration;
    @EqualsAndHashCode.Exclude
    private final Set<Long> userLikesIds = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private final Set<Genre> genres = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private final Mpa mpa;
}
