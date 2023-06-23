package ru.practicum.ewm.app;

import ru.practicum.ewm.app.user.model.User;

public class TestUtil {

    public static User testUser1() {
        return User.builder()
                .id(1L)
                .name("user")
                .email("mail@host.dom")
                .build();
    }
}