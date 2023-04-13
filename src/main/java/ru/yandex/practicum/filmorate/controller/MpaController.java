package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@Slf4j
public class MpaController {

    private final MpaService mpaService;

    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/mpa")
    public List<Mpa> findMpaAll() {
        log.debug("Доступно Мпа рейтингов: {}", mpaService.getAllMpa().size());
        return mpaService.getAllMpa();
    }


    @GetMapping("/mpa/{id}")
    public Mpa findMpaById(@PathVariable("id") int id) {
        return mpaService.getMpaById(id);
    }
}
