package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/genres/{id}")
    public Genre findGenresById(@PathVariable("id") int id) {
        return genreService.getGenresById(id);
    }

    @GetMapping("/genres")
    public List<Genre> findGenresAll() {
        log.debug("Доступно жанров: {}", genreService.getAllGenres().size());
        return genreService.getAllGenres();
    }
}
