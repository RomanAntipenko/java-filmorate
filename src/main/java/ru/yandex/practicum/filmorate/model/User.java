package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
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
    @EqualsAndHashCode.Exclude
    private final Set<Long> friendsIds = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private final Set<Long> followedFriendsIds = new HashSet<>();
/*
    @EqualsAndHashCode.Exclude
    private final Map<Long,String> friends = new HashMap<>();
*/

    /*public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("user_name", name);
        values.put("user_login", login);
        values.put("user_email", email);
        values.put("user_birthday", birthday);
        return values;
    }*/
}
