package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Mpa {
    private int id;
    @EqualsAndHashCode.Exclude
    private String name;


    /*public MpaRating(int ratingId) {
        this.ratingId = ratingId;
        setRatingName(ratingId);
    }

    public void setRatingName(int ratingId) {
        switch (ratingId) {
            case (1):
                this.ratingName = "G";
                break;
            case (2):
                this.ratingName = "PG";
                break;
            case (3):
                this.ratingName = "PG-13";
                break;
            case (4):
                this.ratingName = "R";
                break;
            case (5):
                this.ratingName = "NC-17";
                break;
            *//*default:*//*
     *//*throw new IllegalArgumentException("Такого рейтинга не существует");*//*
        }

    }*/
}
